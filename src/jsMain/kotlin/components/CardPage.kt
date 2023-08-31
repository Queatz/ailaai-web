package components

import Card
import PaddingDefault
import Styles
import androidx.compose.runtime.*
import api
import app.softwork.routingcompose.Router
import appString
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
import notEmpty
import org.jetbrains.compose.web.attributes.autoFocus
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLVideoElement

@Serializable
data class WildReplyBody(
    val message: String,
    val conversation: String?,
    val card: String,
    val device: String,
)

@Serializable
data class ConversationItem(
    var title: String = "",
    var message: String = "",
    var action: ConversationAction? = null,
    var items: MutableList<ConversationItem> = mutableListOf(),
)

@Serializable
data class CardOptions(
    var enableReplies: Boolean? = null,
    var enableAnonymousReplies: Boolean? = null
)

enum class ConversationAction {
    Message
}

@Composable
fun CardPage(cardId: String, onError: () -> Unit = {}, cardLoaded: (card: Card) -> Unit) {
    var isLoading by remember { mutableStateOf(false) }
    var card by remember { mutableStateOf<Card?>(null) }
    var cards by remember { mutableStateOf<List<Card>>(emptyList()) }
    val stack = remember { mutableListOf<ConversationItem>() }
    var cardConversation by remember { mutableStateOf<ConversationItem?>(null) }
    var cardOptions by remember { mutableStateOf<CardOptions?>(null) }
    var isReplying by remember { mutableStateOf<List<ConversationItem>?>(null) }
    var replyMessage by remember { mutableStateOf("") }
    var isSendingReply by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val router = Router.current

    LaunchedEffect(cardId) {
        isReplying = null
        replyMessage = ""
        isLoading = true
        card = null
        cards = emptyList()
        try {
            card = http.get("$baseUrl/cards/$cardId").body()
            cardConversation = card!!.getConversation()
            cardOptions = card!!.getOptions()
            stack.clear()
            cardLoaded(card!!)
            cards = http.get("$baseUrl/cards/$cardId/cards").body()
        } catch (e: Throwable) {
            e.printStackTrace()
            onError()
        } finally {
            isLoading = false
        }
    }

    val sentString = appString { messageWasSent }
    val didntWorkString = appString { didntWork }

    suspend fun sendMessage() {
        isSendingReply = true
        try {
            val body = WildReplyBody(
                message = replyMessage,
                conversation = isReplying!!.map { it.title }.filter { it.isNotBlank() }
                    .notEmpty?.joinToString(" → "),
                card = cardId,
                device = api.token
            )
            http.post("$baseUrl/wild/reply") {
                contentType(ContentType.Application.Json.withCharset(Charsets.UTF_8))
                setBody(body)
            }
            replyMessage = ""
            isReplying = null
            window.alert(sentString)
        } catch (e: Throwable) {
            window.alert(didntWorkString)
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
            Text(appString { cardNotFound })
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
                        CardPhotoOrVideo(card)
                    }
                    Div({
                        classes(Styles.cardContent)
                    }) {
                        card?.let { card ->
                            Div {
                                Div {
                                    NameAndLocation(card.name, card.location)
                                    val viewProfileString = appString { viewProfile }
                                    Span({
                                        classes("material-symbols-outlined")
                                        title(viewProfileString)
                                        style {
                                            cursor("pointer")
                                            opacity(.5f)
                                            marginLeft(.25.cssRem)
                                            property("vertical-align", "text-bottom")
                                        }
                                        onClick { event ->
                                            if (event.ctrlKey) {
                                                window.open("/profile/${card.person}", target = "_blank")
                                            } else {
                                                router.navigate("/profile/${card.person}")
                                            }
                                        }
                                    }) {
                                        Text("person")
                                    }
                                }
                                card.categories?.firstOrNull()?.let { category ->
                                    Div({
                                        classes(Styles.category)
                                        style {
                                            property("clear", "both")
                                        }
                                    }) {
                                        Text(category)
                                    }
                                }
                            }
                            cardConversation?.message?.let { message ->
                                Div({
                                    style {
                                        whiteSpace("pre-wrap")
                                    }
                                }) {
                                    LinkifyText(message)
                                }
                            }
                            if (isReplying != null) {
                                val includeContactString = appString { includeContact }
                                TextArea(replyMessage) {
                                    classes(Styles.textarea)
                                    style {
                                        width(100.percent)
                                        height(8.cssRem)
                                        marginBottom(1.cssRem)
                                    }

                                    placeholder(includeContactString)

                                    if (isSendingReply) {
                                        disabled()
                                    }

                                    onInput {
                                        replyMessage = it.value
                                    }

                                    autoFocus()
                                }
                            }
                            if (isReplying != null) {
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
                                        Text(appString { sendMessage })
                                    }
                                    Button({
                                        classes(Styles.outlineButton)
                                        onClick {
                                            isReplying = null
                                        }
                                        if (isSendingReply) {
                                            disabled()
                                        }
                                    }) {
                                        Text(appString { cancel })
                                    }
                                }
                            } else {
                                cardConversation?.items?.forEach { item ->
                                    when (item.action) {
                                        ConversationAction.Message -> {
                                            Button({
                                                classes(Styles.button)
                                                onClick {
                                                    isReplying = stack + (cardConversation?.let(::listOf) ?: emptyList()) + item.let(::listOf)
                                                }
                                            }) {
                                                Span({
                                                    classes("material-symbols-outlined")
                                                }) {
                                                    Text("message")
                                                }
                                                Text(" ${item.title}")
                                            }
                                        }
                                        else -> {
                                            Button({
                                                classes(Styles.outlineButton)
                                                onClick {
                                                    stack.add(cardConversation!!)
                                                    cardConversation = item
                                                }
                                            }) {
                                                Text(item.title)
                                            }
                                        }
                                    }
                                }
                                if (cardConversation?.items.isNullOrEmpty() && cardOptions?.enableAnonymousReplies != false) {
                                    Button({
                                        classes(Styles.button)
                                        onClick {
                                            isReplying = stack + (cardConversation?.let(::listOf) ?: emptyList())
                                        }
                                    }) {
                                        Span({
                                            classes("material-symbols-outlined")
                                        }) {
                                            Text("message")
                                        }
                                        Text(" ${appString { message }}")
                                    }
                                }
                                if (stack.isNotEmpty()) {
                                    Button({
                                        classes(Styles.outlineButton)
                                        onClick {
                                            cardConversation = stack.removeLast()
                                        }
                                    }) {
                                        Text(appString { goBack })
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
                    CardItem(card, styles = {
                        margin(PaddingDefault)
                    })
                }
            }
        }
    }
}

fun Card.getConversation() = DefaultJson.decodeFromString<ConversationItem>(conversation ?: "{}")

fun Card.getOptions() = DefaultJson.decodeFromString<CardOptions>(options ?: "{}")
