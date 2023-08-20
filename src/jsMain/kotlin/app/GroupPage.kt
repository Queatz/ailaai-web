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
import io.ktor.http.*
import io.ktor.utils.io.charsets.*
import json
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import org.jetbrains.compose.web.attributes.autoFocus
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.dom.TextArea
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.SMOOTH
import org.w3c.dom.ScrollBehavior
import org.w3c.dom.ScrollToOptions

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
                    IconButton("mic", "Record audio", styles = { marginLeft(1.cssRem) }) {
                        // todo
                    }
                    IconButton("image", "Send photo", styles = { marginLeft(1.cssRem) }) {
                        // todo
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
            messages.forEach {
                MessageItem(
                    it,
                    group.members?.find { member -> member.member?.id == it.member },
                    myMember
                )
            }
        }
    }
}
