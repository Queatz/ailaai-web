package components

import Card
import PaddingDefault
import Styles
import androidx.compose.runtime.*
import api
import app.softwork.routingcompose.Router
import baseUrl
import http
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.utils.io.charsets.*
import kotlinx.browser.window
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import org.jetbrains.compose.web.attributes.autoFocus
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*

@Serializable
data class WildReplyBody(val message: String, val conversation: String?, val card: String, val device: String)

@Serializable
data class ConversationItem(
    var title: String = "",
    var message: String = "",
    var items: MutableList<ConversationItem> = mutableListOf(),
)

@Composable
fun CardPage(cardId: String, onError: () -> Unit = {}, cardLoaded: (card: Card) -> Unit) {
    var isLoading by remember { mutableStateOf(false) }
    var card by remember { mutableStateOf<Card?>(null) }
    var cards by remember { mutableStateOf<List<Card>>(emptyList()) }
    val stack = remember { mutableListOf<ConversationItem>() }
    var cardConversation by remember { mutableStateOf<ConversationItem?>(null) }
    var isReplying by remember { mutableStateOf(false) }
    var replyMessage by remember { mutableStateOf("") }
    var isSendingReply by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val router = Router.current

    LaunchedEffect(cardId) {
        isReplying = false
        replyMessage = ""
        isLoading = true
        card = null
        cards = emptyList()
        try {
            card = http.get("$baseUrl/cards/$cardId").body()
            cardConversation = card!!.getConversation()
            stack.clear()
            cardLoaded(card!!)
            cards = http.get("$baseUrl/cards/$cardId/cards").body()
        } catch (e: Exception) {
            e.printStackTrace()
            onError()
        } finally {
            isLoading = false
        }
    }

    suspend fun sendMessage() {
        isSendingReply = true
        try {
            val body = WildReplyBody(
                message = replyMessage,
                conversation = (stack.map { it.title } + cardConversation?.title).filter { it.isNullOrBlank().not() }
                    .takeIf { it.isNotEmpty() }?.joinToString(" → "),
                card = cardId,
                device = api.token
            )
            http.post("$baseUrl/wild/reply") {
                contentType(ContentType.Application.Json.withCharset(Charsets.UTF_8))
                setBody(body)
            }
            replyMessage = ""
            isReplying = false
            window.alert("Your message was sent!")
        } catch (e: Exception) {
            window.alert("That didn't work")
            e.printStackTrace()
        } finally {
            isSendingReply = false
        }
    }

    if (!isLoading && card == null) {
        Div({
            classes(Styles.mainContent)
            style {
                display(DisplayStyle.Flex)
                minHeight(100.vh)
                width(100.percent)
                flexDirection(FlexDirection.Column)
                padding(PaddingDefault * 2)
                alignItems(AlignItems.Center)
                justifyContent(JustifyContent.FlexStart)
            }
        }) {
            Text("Card not found.")
        }
    } else {
        Div({
            classes(Styles.mainContent)
        }) {
            Div({
                classes(Styles.navContainer)
            }) {
                Div({
                    classes(Styles.navContent)
                }) {
                    card?.let { card ->
                        card.photo?.let {
                            Div({
                                style {
                                    width(100.percent)
                                    backgroundColor(Styles.colors.background)
                                    backgroundImage("url($baseUrl$it)")
                                    backgroundPosition("center")
                                    backgroundSize("cover")
                                    property("aspect-ratio", "2")
                                }
                            }) {}
                        }
                    }
                    Div({
                        classes(Styles.cardContent)
                    }) {
                        card?.let { card ->
                            Div {
                                NameAndLocation(card.name, card.location)
                            }
                            cardConversation?.message?.let { message ->
                                Div({
                                    style {
                                        whiteSpace("pre-wrap")
                                    }
                                }) {
                                    Text(message)
                                }
                            }
                            if (isReplying) {
                                TextArea(replyMessage) {
                                    style {
                                        width(100.percent)
                                        height(8.cssRem)
                                        borderRadius(1.cssRem)
                                        border(1.px, LineStyle.Solid, Styles.colors.background)
                                        property("resize", "none")
                                        padding(1.cssRem)
                                        property("font-size", "inherit")
                                        fontFamily("inherit")
                                        boxSizing("border-box")
                                    }

                                    placeholder("Be sure you include a way to contact you!")

                                    if (isSendingReply) {
                                        disabled()
                                    }

                                    onInput {
                                        replyMessage = it.value
                                    }

                                    autoFocus()
                                }
                            }
                            if (isReplying) {
                                Div({
                                    style {
                                        display(DisplayStyle.Flex)
                                        marginBottom(0.cssRem)
                                    }
                                }) {
                                    Button({
                                        classes(Styles.button)
                                        style {
                                            marginRight(1.cssRem)
                                        }
                                        onClick {
                                            scope.launch {
                                                sendMessage()
                                            }
                                        }
                                        if (isSendingReply || replyMessage.isBlank()) {
                                            disabled()
                                        }
                                    }) {
                                        Text("Send message")
                                    }
                                    Button({
                                        classes(Styles.outlineButton)
                                        onClick {
                                            isReplying = false
                                        }
                                        if (isSendingReply) {
                                            disabled()
                                        }
                                    }) {
                                        Text("Cancel")
                                    }
                                }
                            } else {
                                cardConversation?.items?.forEach { item ->
                                    Button({
                                        classes(Styles.button)
                                        onClick {
                                            stack.add(cardConversation!!)
                                            cardConversation = item
                                        }
                                    }) {
                                        Text(item.title)
                                    }
                                }
                                if (cardConversation?.items.isNullOrEmpty()) {
                                    Button({
                                        classes(Styles.button)
                                        onClick {
                                            //                                    window.open("/", target = "_blank")
                                            isReplying = true
                                        }
                                    }) {
                                        Span({
                                            classes("material-symbols-outlined")
                                        }) {
                                            Text("message")
                                        }
                                        Text(" Message")
                                    }
                                }
                                if (stack.isNotEmpty()) {
                                    Button({
                                        classes(Styles.outlineButton)
                                        onClick {
                                            cardConversation = stack.removeLast()
                                        }
                                    }) {
                                        Text("Go back")
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Div({
                classes(Styles.content)
            }) {
                cards.forEach { card ->
                    CardItem(card, router)
                }
            }
        }
    }
}

fun Card.getConversation() = DefaultJson.decodeFromString<ConversationItem>(conversation ?: "{}")
