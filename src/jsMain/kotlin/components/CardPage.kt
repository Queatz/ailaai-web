package components

import Card
import CornerDefault
import PaddingDefault
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
    var items: MutableList<ConversationItem> = mutableListOf()
)

@Composable
fun CardNameAndLocation(card: Card?) {
    Span({
        style {
            fontWeight("bold")
            fontSize(24.px)
        }
    }) {
        Text(card?.name ?: "")
    }
    Span({
        style {
            marginLeft(PaddingDefault / 2)
            fontSize(18.px)
            opacity(.75f)
        }
    }) {
        Text(card?.location ?: "")
    }
}


@Composable
fun CardPage(cardId: String, cardLoaded: (card: Card) -> Unit) {
    var isLoading by remember { mutableStateOf(false) }

    var card by remember { mutableStateOf<Card?>(null) }
    var cards by remember { mutableStateOf<List<Card>>(emptyList()) }
    val stack = remember { mutableListOf<ConversationItem>() }
    var cardConversation by remember { mutableStateOf<ConversationItem?>(null) }
    var isReplying by remember { mutableStateOf(false) }
    var replyMessage by remember { mutableStateOf("") }
    var isSendingReply by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(cardId) {
        isSendingReply = false
        replyMessage = ""
        isLoading = true
        cards = emptyList()
        try {
            card = http.get("$baseUrl/cards/$cardId").body()
            cardConversation = card!!.getConversation()
            stack.clear()
            cardLoaded(card!!)
            cards = http.get("$baseUrl/cards/$cardId/cards").body()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    suspend fun sendReply() {
        isSendingReply = true
        try {
            val body = WildReplyBody(
                message = replyMessage,
                conversation = (stack.map { it.title } + cardConversation?.title).filter { it.isNullOrBlank().not() }.takeIf { it.isNotEmpty() }?.joinToString(" → "),
                card = cardId,
                device = api.token
            )
            http.post("$baseUrl/wild/reply") {
                contentType(ContentType.Application.Json.withCharset(Charsets.UTF_8))
                setBody(body)
            }
            replyMessage = ""
            isReplying = false
            window.alert("Your reply was sent!")
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
                    val router = Router.current
                    card?.parent?.let { cardParentId ->
                        Button({
                            classes(Styles.textButton)
                            onClick {
                                router.navigate("/card/$cardParentId")
                            }
                        }) {
                            Span({
                                classes("material-symbols-outlined")
                            }) {
                                Text("arrow_back")
                            }
                            Text(" Go back")
                        }
                    }
                    card?.let { card ->
                        card.photo?.let {
                            Div({
                                style {
                                    width(100.percent)
                                    backgroundColor(Styles.colors.background)
                                    backgroundImage("url($baseUrl${it})")
                                    backgroundPosition("center")
                                    backgroundSize("cover")
                                    borderRadius(CornerDefault)
                                    property("aspect-ratio", "1.5")
                                }
                            }) {}
                        }
                        Div {
                            CardNameAndLocation(card)
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

                                placeholder("Message")

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
                                            sendReply()
                                        }
                                    }
                                    if (isSendingReply || replyMessage.isBlank()) {
                                        disabled()
                                    }
                                }) {
                                    Text("Send reply")
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
                            Div({
                                style {
                                    opacity(.5f)
                                }
                            }) {
                                Text("Be sure you include a way to contact you!")
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
                                        Text("mail")
                                    }
                                    Text(" Reply")
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
            Div({
                classes(Styles.content)
            }) {
                val router = Router.current

                if (cards.isEmpty() && !isLoading) {
                    Div({
                        style {
                            padding(PaddingDefault)
                        }
                    }) {
//                        Text("No cards.")
                    }
                }

                cards.forEach { card ->
                    Div({
                        classes(Styles.card)
                        style {
                            if (card.photo != null) {
                                backgroundImage("url($baseUrl${card.photo!!})")
                                backgroundPosition("center")
                                backgroundSize("cover")
                            }

                            position(Position.Relative)
                        }
                        onClick { event ->
                            if (event.ctrlKey) {
                                window.open("/card/${card.id}", target = "_blank")
                            } else {
                                router.navigate("/card/${card.id}")
                            }
                        }
                    }) {
                        card.cardCount?.takeIf { it > 0 }?.let {
                            Div({
                                style {
                                    backgroundColor(rgba(255, 255, 255, .8))
                                    borderRadius(CornerDefault * 2)
                                    padding(PaddingDefault / 2, PaddingDefault)
                                    color(Color.black)
                                    position(Position.Absolute)
                                    top(PaddingDefault)
                                    right(PaddingDefault)
                                }
                            }) {
                                Text("$it ${if (it == 1) "card" else "cards"}")
                            }
                        }
                        Div({
                            style {
                                backgroundColor(rgba(255, 255, 255, .8))
                                padding(PaddingDefault)
                                color(Color.black)
                                maxHeight(50.percent)
                                boxSizing("border-box")
                                overflowY("auto")
                                fontSize(18.px)
                            }
                        }) {
                            Div({
                                style {
                                    marginBottom(PaddingDefault)
                                }
                            }) {
                                CardNameAndLocation(card)
                            }

                            card.getConversation().message.takeIf { it.isNotEmpty() }
                                ?.let { conversationMessage ->
                                    Div({
                                        style {
                                            whiteSpace("pre-wrap")
                                        }
                                    }) {
                                        Text(conversationMessage)
                                    }
                                }
                        }
                    }
                }
            }
        }
    }
}

fun Card.getConversation() = DefaultJson.decodeFromString<ConversationItem>(conversation ?: "{}")
