package app

import CornerDefault
import GroupExtended
import Message
import Sticker
import Styles
import androidx.compose.runtime.*
import app.messaages.MessageItem
import app.messaages.StickerAttachment
import appString
import application
import baseUrl
import components.IconButton
import components.Loading
import http
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.util.*
import io.ktor.utils.io.charsets.*
import json
import kotlinx.browser.document
import kotlinx.coroutines.await
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
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
import kotlin.js.Promise

@Composable
fun GroupPage(group: GroupExtended?) {
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

    var isLoading by remember(group) {
        mutableStateOf(true)
    }

    var showStickers by remember(group) {
        mutableStateOf(false)
    }

    var messagesDiv by remember {
        mutableStateOf<HTMLDivElement?>(null)
    }

    suspend fun reload() {
        if (group == null) return
        try {
            messages = http.get("$baseUrl/groups/${group.group?.id}/messages") {
                contentType(ContentType.Application.Json.withCharset(Charsets.UTF_8))
                bearerAuth(application.bearer!!)
            }.body()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        isLoading = false

        if (messages.lastOrNull()?.id != latestMessage) {
            latestMessage = messages.lastOrNull()?.id
            scope.launch {
                delay(100)
                messagesDiv?.scroll(ScrollToOptions(top = messagesDiv!!.scrollHeight.toDouble(), behavior = ScrollBehavior.SMOOTH))
            }
        }
    }

    LaunchedEffect(group) {
        reload()
    }

    LaunchedEffect(group) {
        push.events.collectLatest {
            reload()
        }
    }

    fun sendPhotos(files: List<File>, message: Message? = null) {
        scope.launch {
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
            try {
                http.post("$baseUrl/groups/${group!!.group!!.id!!}/photos") {
                    //contentType(ContentType.Application.Json.withCharset(Charsets.UTF_8))
                    bearerAuth(application.bearer!!)
                    setBody(
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
                    )
                }
                reload()
            } catch (e: Exception) {
                e.printStackTrace()
            }
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

        scope.launch {
            try {
                http.post("$baseUrl/groups/${group!!.group!!.id!!}/messages") {
                    contentType(ContentType.Application.Json.withCharset(Charsets.UTF_8))
                    bearerAuth(application.bearer!!)
                    setBody(Message(text = text))
                }
                reload()
            } catch (e: Exception) {
                e.printStackTrace()
                if (messageText.isBlank()) {
                    messageText = text
                }
            }
        }
    }

    fun sendSticker(sticker: Sticker) {
        scope.launch {
            try {
                http.post("$baseUrl/groups/${group!!.group!!.id!!}/messages") {
                    contentType(ContentType.Application.Json.withCharset(Charsets.UTF_8))
                    bearerAuth(application.bearer!!)
                    setBody(Message(
                        attachment = json.encodeToString(
                            StickerAttachment(
                                sticker.photo,
                                sticker.id,
                                sticker.message
                            )
                        )
                    ))
                }
                reload()
            } catch (e: Exception) {
                e.printStackTrace()
            }
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
    }
    else if (isLoading) {
        Loading()
    } else {
        val myMember = group.members?.find { it.person?.id == me!!.id }

        if (showStickers) {
            Div({
                style {
                    display(DisplayStyle.Flex)
                    height(18.cssRem)
                    maxHeight(50.vh)
                    overflowX("hidden")
                    overflowY("auto")
                    flexDirection(FlexDirection.Column)
                    backgroundColor(Color("#fafafa"))
                    border(1.px, LineStyle.Solid, Color("#e4e4e4"))
                    marginLeft(1.cssRem)
                    marginRight(1.cssRem)
                    marginBottom(1.cssRem)
                    borderRadius(CornerDefault)
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
                    IconButton(if (showStickers) "expand_less" else "expand_more", "More", styles = { marginLeft(1.cssRem) }) {
                        showStickers = !showStickers
                    }
                } else {
                    IconButton("send", "Send message", styles = { marginLeft(1.cssRem) }) {
                        // todo
                    }
                }
            }
            val messageString = appString { message }
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
                        it.stopPropagation()
                    }
                }

                onInput {
                    messageText = it.value
                    it.target.style.height = "0"
                    it.target.style.height = "${it.target.scrollHeight + 2}px"
                }

                autoFocus()
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
    }
}
