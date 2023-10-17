import androidx.compose.runtime.*
import app.AppStyles
import app.StickersTray
import app.group.GroupTopBar
import app.messaages.MessageItem
import app.messaages.StickerAttachment
import components.IconButton
import components.Loading
import io.ktor.client.request.forms.*
import io.ktor.http.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import org.jetbrains.compose.web.attributes.autoFocus
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.TextArea
import org.w3c.dom.*
import org.w3c.files.File

@Composable
fun GroupLayout(
    group: GroupExtended,
    onGroupUpdated: () -> Unit,
    onGroupGone: () -> Unit
) {
    val me by application.me.collectAsState()
    val myMember = group.members?.find { it.person?.id == me!!.id }

    LaunchedEffect(group.group?.id) {
        group.group?.id?.let { groupId ->
            // Mark group as read
            api.group(groupId) {}
        }
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    val scope = rememberCoroutineScope()
    var messageText by remember {
        mutableStateOf("")
    }

    var messages by remember {
        mutableStateOf(emptyList<Message>())
    }

    var latestMessage by remember {
        mutableStateOf<String?>(null)
    }

    var isSending by remember(group) {
        mutableStateOf(false)
    }

    var messagesDiv by remember {
        mutableStateOf<HTMLDivElement?>(null)
    }
    var showStickers by remember(group) {
        mutableStateOf(false)
    }

    suspend fun reloadMessages() {
        api.groupMessages(group.group!!.id!!) {
            messages = it
        }
        isLoading = false

        if (messages.lastOrNull()?.id != latestMessage) {
            latestMessage = messages.lastOrNull()?.id
            scope.launch {
                delay(100)
                messagesDiv?.scroll(
                    ScrollToOptions(
                        top = messagesDiv!!.scrollHeight.toDouble(),
                        behavior = ScrollBehavior.SMOOTH
                    )
                )
            }
        }
    }

    LaunchedEffect(group.group?.id) {
        isLoading = true
        reloadMessages()
    }

    LaunchedEffect(group.group?.id) {
        push.events.collectLatest {
            reloadMessages()
        }
    }

    LaunchedEffect(group.group?.id) {
        push.reconnect.collectLatest {
            reloadMessages()
        }
    }

    fun sendPhotos(files: List<File>, message: Message? = null) {
        isSending = true
        scope.launch {
            try {
                val photos = files.map { it.toBytes() }

                api.sendPhotos(
                    group.group!!.id!!,
                    MultiPartFormDataContent(
                        formData {
                            if (message != null) {
                                append("message", json.encodeToString(message))
                            }
                            photos.forEachIndexed { index, photo ->
                                append(
                                    "photo[$index]",
                                    photo,
                                    Headers.build {
                                        append(HttpHeaders.ContentType, "image/jpeg")
                                        append(HttpHeaders.ContentDisposition, "filename=photo.jpg")
                                    }
                                )
                            }
                        }
                    )
                ) {
                    reloadMessages()
                }
            } catch (e: Throwable) {
                e.printStackTrace()
                return@launch
            }

            isSending = false
        }
    }

    fun sendMessage() {
        if (messageText.isBlank()) return

        val text = messageText
        messageText = ""

        isSending = true

        scope.launch {
            api.sendMessage(
                group.group!!.id!!,
                Message(text = text),
                onError = {
                    if (messageText.isBlank()) {
                        messageText = text
                    }
                }
            ) {
                reloadMessages()
            }

            isSending = false
        }
    }

    fun sendSticker(sticker: Sticker) {
        isSending = true
        scope.launch {
            api.sendMessage(
                group.group!!.id!!,
                Message(
                    attachment = json.encodeToString(
                        StickerAttachment(
                            sticker.photo,
                            sticker.id,
                            sticker.message
                        )
                    )
                )
            ) {
                reloadMessages()
            }

            isSending = false
        }
    }

    if (showStickers) {
        Div({
            classes(AppStyles.tray)
            style {
                marginLeft(1.r)
                marginRight(1.r)
                marginBottom(1.r)
            }
        }) {
            StickersTray {
                sendSticker(it)
                showStickers = false
            }
        }
    }

    if (isLoading) {
        Loading()
    } else {
        Div({
            classes(AppStyles.messageBar)
        }) {
            Div({
                style {
                    flexShrink(0)
                    display(DisplayStyle.Flex)
                }
            }) {
                if (messageText.isBlank()) {
//                    IconButton("mic", "Record audio", styles = { marginLeft(1.r) }) {
//                        // todo
//                    }
                    IconButton("image", "Send photo", styles = { marginLeft(1.r) }) {
                        pickPhotos {
                            sendPhotos(it)
                        }
                    }
                    IconButton(if (showStickers) "expand_less" else "expand_more", "Stickers", styles = {
                        marginLeft(1.r)
                        marginRight(1.r)
                    }) {
                        showStickers = !showStickers
                    }
                } else {
                    IconButton("send", "Send message", styles = { marginLeft(1.r) }) {
                        // todo
                    }
                }
            }
            val messageString = if (isSending) appString { sending } else appString { message }
            // todo can be EditField
            TextArea(messageText) {
                classes(Styles.textarea)
                style {
                    width(100.percent)
                    height(3.5.r)
                    maxHeight(6.5.r)
                }

                placeholder(messageString)

                onKeyDown {
                    if (it.key == "Enter" && !it.shiftKey) {
                        sendMessage()
                        it.preventDefault()
                        scope.launch {
                            delay(1)
                            (it.target as HTMLTextAreaElement).style.height = "0"
                            (it.target as HTMLTextAreaElement).style.height =
                                "${(it.target as HTMLTextAreaElement).scrollHeight + 2}px"
                        }
                    }
                }

                onInput {
                    messageText = it.value
                    it.target.style.height = "0"
                    it.target.style.height = "${it.target.scrollHeight + 2}px"
                }

                onChange {
                    it.target.style.height = "0"
                    it.target.style.height = "${it.target.scrollHeight + 2}px"
                }

                onPaste {
                    val items = it.clipboardData?.items ?: return@onPaste

                    val photos = (0 until items.length).mapNotNull {
                        items[it]
                    }.filter {
                        it.type.startsWith("image/")
                    }.mapNotNull {
                        it.getAsFile()
                    }

                    if (photos.isEmpty()) return@onPaste

                    sendPhotos(photos)
                }

                autoFocus()

                ref { element ->
                    element.focus()
                    onDispose {}
                }
            }
        }
        Div({
            classes(AppStyles.messages)
            ref {
                messagesDiv = it

                onDispose {
                    messagesDiv = null
                }
            }
        }) {
            messages.forEachIndexed { index, it ->
                MessageItem(
                    it,
                    if (index < messages.lastIndex - 1) messages[index + 1] else null,
                    group.members?.find { member -> member.member?.id == it.member },
                    myMember
                )
            }
        }

        GroupTopBar(
            group,
            onGroupUpdated = onGroupUpdated,
            onGroupGone = onGroupGone
        )
    }
}
