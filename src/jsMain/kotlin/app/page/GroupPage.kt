package app.page

import Group
import GroupExtended
import Message
import Sticker
import Styles
import androidx.compose.runtime.*
import api
import app.AppStyles
import app.PageTopBar
import app.StickersTray
import app.menu.Menu
import app.messaages.MessageItem
import app.messaages.StickerAttachment
import app.nav.name
import appString
import application
import components.IconButton
import components.Loading
import io.ktor.client.request.forms.*
import io.ktor.http.*
import json
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import lib.formatDistanceToNow
import org.jetbrains.compose.web.attributes.autoFocus
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.dom.TextArea
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get
import org.w3c.dom.*
import org.w3c.files.File
import push
import kotlin.js.Date
import kotlin.js.Promise

@Composable
fun GroupPage(group: GroupExtended?, onGroupUpdated: () -> Unit) {
    val me by application.me.collectAsState()
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

    var isLoading by remember(group?.group?.id) {
        mutableStateOf(true)
    }

    var isSending by remember(group) {
        mutableStateOf(false)
    }

    var showStickers by remember(group) {
        mutableStateOf(false)
    }

    var messagesDiv by remember {
        mutableStateOf<HTMLDivElement?>(null)
    }

    LaunchedEffect(group?.group?.id) {
        group?.group?.id?.let { groupId ->
            // Mark group as read
            api.group(group.group!!.id!!) {}
        }
    }

    suspend fun reloadMessages() {
        if (group == null) return
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

    LaunchedEffect(group) {
        reloadMessages()
    }

    LaunchedEffect(group) {
        push.events.collectLatest {
            reloadMessages()
        }
    }

    LaunchedEffect(group) {
        push.reconnect.collectLatest {
            reloadMessages()
        }
    }

    fun sendPhotos(files: List<File>, message: Message? = null) {
        isSending = true
        scope.launch {
            try {
                val photos = files.map {
                    val reader = it.asDynamic().stream().getReader()
                    var bytes = ByteArray(0)
                    while (true) {
                        val chunk = (reader.read() as Promise<*>).await().asDynamic()
                        val value = chunk.value as? Uint8Array
                        if (value != null) {
                            bytes += ByteArray(value.length) { value[it] }
                        }
                        if (chunk.done == true) {
                            break
                        }
                    }
                    bytes
                }

                api.sendPhotos(
                    group!!.group!!.id!!,
                    MultiPartFormDataContent(formData {
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
                    })
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

    fun pickPhoto() {
        val element = (document.createElement("input") as HTMLInputElement)
        element.type = "file"
        element.multiple = true
        element.accept = "image/*"
        element.addEventListener("change", {
            if (element.files != null) {
                sendPhotos(element.files!!.asList())
            }
        })
        element.click()
    }

    fun sendMessage() {
        if (messageText.isBlank()) return

        val text = messageText
        messageText = ""

        isSending = true

        scope.launch {
            api.sendMessage(
                group!!.group!!.id!!,
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
                group!!.group!!.id!!,
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

    if (group == null) {
        Div({
            style {
                height(100.percent)
                display(DisplayStyle.Flex)
                alignItems(AlignItems.Center)
                justifyContent(JustifyContent.Center)
                opacity(.5)
            }
        }) {
            Text("Select a group")
        }
    } else if (isLoading) {
        Loading()
    } else {
        val myMember = group.members?.find { it.person?.id == me!!.id }

        if (showStickers) {
            Div({
                classes(AppStyles.tray)
                style {
                    marginLeft(1.cssRem)
                    marginRight(1.cssRem)
                    marginBottom(1.cssRem)
                }
            }) {
                StickersTray {
                    sendSticker(it)
                    showStickers = false
                }
            }
        }

        Div({
            classes(AppStyles.messageBar)
        }) {
            Div({
                style {
                    flexShrink(0)
                }
            }) {
                if (messageText.isBlank()) {
//                    IconButton("mic", "Record audio", styles = { marginLeft(1.cssRem) }) {
//                        // todo
//                    }
                    IconButton("image", "Send photo", styles = { marginLeft(1.cssRem) }) {
                        pickPhoto()
                    }
                    IconButton(if (showStickers) "expand_less" else "expand_more", "Stickers", styles = {
                        marginLeft(1.cssRem)
                        marginRight(1.cssRem)
                    }) {
                        showStickers = !showStickers
                    }
                } else {
                    IconButton("send", "Send message", styles = { marginLeft(1.cssRem) }) {
                        // todo
                    }
                }
            }
            val messageString = if (isSending) appString { sending } else appString { message }
            TextArea(messageText) {
                classes(Styles.textarea)
                style {
                    width(100.percent)
                    height(3.5.cssRem)
                    maxHeight(6.5.cssRem)
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

        GroupTopBar(group) {
            onGroupUpdated()
        }
    }
}

@Composable
fun GroupTopBar(group: GroupExtended, onGroupUpdated: () -> Unit) {
    val me by application.me.collectAsState()
    val myMember = group.members?.find { it.person?.id == me?.id }
    val scope = rememberCoroutineScope()

    var menuTarget by remember {
        mutableStateOf<DOMRect?>(null)
    }

    fun renameGroup() {
        scope.launch {
            val name = window.prompt("Group name", group.group?.name ?: "")

            if (name == null) return@launch

            api.updateGroup(group.group!!.id!!, Group(name = name)) {
                onGroupUpdated()

            }
        }
    }

    if (menuTarget != null) {
        Menu({ menuTarget = null }, menuTarget!!) {
//            item("Pin") {
//
//            }
            item("Rename") {
                renameGroup()
            }
        }
    }

    PageTopBar(
        group.name("Someone", "New group", listOf(me!!.id!!)),
        group.members?.filter { it != myMember }?.maxByOrNull {
            it.person?.seen?.let { Date(it).getTime() } ?: 0.0
        }?.person?.seen?.let { Date(it) }?.let {
            "Active ${formatDistanceToNow(it, js("{ addSuffix: true }"))}"
        }
    ) {
        menuTarget = if (menuTarget == null) (it.target as HTMLElement).getBoundingClientRect() else null
    }
}
