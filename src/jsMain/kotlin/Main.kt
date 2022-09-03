import androidx.compose.runtime.*
import app.softwork.routingcompose.BrowserRouter
import app.softwork.routingcompose.Router
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.js.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.renderComposableInBody

const val baseUrl = "https://api.ailaai.app"

val http = HttpClient(Js) {
    install(ContentNegotiation) { json(Json) }
}

@Serializable
class Card(
    var person: String? = null,
    var parent: String? = null,
    var name: String? = null,
    var photo: String? = null,
    var location: String? = null,
    var equipped: Boolean? = null,
    var geo: List<Double>? = null,
    var conversation: String? = null,
    var active: Boolean? = null,
    var cardCount: Int? = null
) : Model()

@Serializable
open class Model {
    var id: String? = null
    var createdAt: String? = null
}

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
        }
    }) {
        Text(card?.name ?: "")
    }
    Span({
        style {
            marginLeft(PaddingDefault / 2)
            fontSize(14.px)
            opacity(.75f)
        }
    }) {
        Text(card?.location ?: "")
    }
}

fun main() {
    renderComposableInBody {
        Style(Styles)

        var isLoading by remember { mutableStateOf(false) }
        var card by remember { mutableStateOf<Card?>(null) }
        var cards by remember { mutableStateOf<List<Card>>(emptyList()) }
        val stack = remember { mutableListOf<ConversationItem>() }
        var cardConversation by remember { mutableStateOf<ConversationItem?>(null) }

        BrowserRouter("") {
            route("card") {
                string { cardId ->
                    LaunchedEffect(cardId) {
                        isLoading = true
                        try {
                            card = http.get("$baseUrl/cards/$cardId").body()
                            cardConversation = card!!.getConversation()
                            cards = http.get("$baseUrl/cards/$cardId/cards").body()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        } finally {
                            isLoading = false
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
                            style {
                                display(DisplayStyle.Flex)
                                minHeight(100.vh)
                                width(100.percent)
                                alignItems(AlignItems.Stretch)
                                justifyContent(JustifyContent.Stretch)
                            }
                        }) {
                            Div({
                                classes(Styles.navContent)
                                style {
                                    display(DisplayStyle.Flex)
                                    flexDirection(FlexDirection.Column)
                                    padding(PaddingDefault)
                                    backgroundColor(Styles.colors.background)
                                    overflowX("hidden")
                                }
                            }) {
                                card?.let { card ->
                                    card.photo?.let {
                                        Div({
                                            style {
                                                width(100.percent)
                                                backgroundImage("url($baseUrl${it})")
                                                backgroundPosition("center")
                                                backgroundSize("cover")
                                                borderRadius(CornerDefault)
                                                marginBottom(PaddingDefault)
                                                property("aspect-ratio", "1.5")
                                            }
                                        }) {}
                                    }
                                    Div({
                                        style {
                                            marginBottom(PaddingDefault)
                                        }
                                    }) {
                                        CardNameAndLocation(card)
                                    }
                                    Div({
                                        style {
                                            marginBottom(PaddingDefault)
                                            whiteSpace("pre-wrap")
                                        }
                                    }) {
                                        Text(cardConversation?.message ?: "")
                                    }
                                    cardConversation?.items?.takeIf { it.isNotEmpty() }?.forEach { item ->
                                        Button({
                                            classes(Styles.button)
                                            style {
                                                marginBottom(PaddingDefault)
                                            }
                                            onClick {
                                                stack.add(cardConversation!!)
                                                cardConversation = item
                                            }
                                        }) {
                                            Text(item.title)
                                        }
                                    }
                                    if (stack.isNotEmpty()) {
                                        Button({
                                            classes(Styles.outlineButton)
                                            style {
                                                marginBottom(PaddingDefault)
                                            }
                                            onClick {
                                                cardConversation = stack.removeLast()
                                            }
                                        }) {
                                            Text("Go back")

                                        }
                                    }
                                }
                            }
                            Div({
                                classes(Styles.content)
                                style {
                                    display(DisplayStyle.Flex)
                                    flexDirection(FlexDirection.Row)
                                    flexWrap(FlexWrap.Wrap)
                                    flexGrow(1)
                                    padding(PaddingDefault)
                                    overflow("auto")
                                    padding(PaddingDefault / 2)
                                    justifyContent(JustifyContent.FlexStart)
                                    alignContent(AlignContent.FlexStart)
                                }
                            }) {
                                val router = Router.current

                                if (cards.isEmpty() && !isLoading) {
                                    Div({
                                        style {
                                            padding(PaddingDefault)
                                        }
                                    }) {
                                        Text("No cards.")
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
                                        onClick {
                                            router.navigate("/card/${card.id}")
                                        }
                                    }) {
                                        card.cardCount?.takeIf { it > 0 }?.let {
                                            Div({
                                                style {
                                                    backgroundColor(rgba(0, 0, 0, .8))
                                                    borderRadius(CornerDefault * 2)
                                                    padding(PaddingDefault / 2, PaddingDefault)
                                                    color(Color.white)
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
                                                backgroundColor(rgba(0, 0, 0, .8))
                                                padding(PaddingDefault)
                                                color(Color.white)
                                                maxHeight(50.percent)
                                                boxSizing("border-box")
                                                overflowY("auto")
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
            }

            noMatch {
                val router = Router.current

                Div({
                    style {
                        margin(PaddingDefault)
                        padding(1.5.cssRem)
                        borderRadius(1.cssRem)
                        backgroundColor(Styles.colors.background)
                        fontSize(18.px)
                    }

                }) {
                    H2({
                        style {
                            marginTop(0.cssRem)
                        }
                    }) {
                        Text("1. Enable Unknown Sources to install apps from your browser")
                    }
                    A("https://duckduckgo.com/?q=Enable+Unknown+Sources+on+Android", {
                        style {
                            textDecoration("none")
                            color(Color("#006689"))
                            fontWeight(700)
                            fontSize(18.px)
                        }
                    }) {
                        Text("Learn how to enable Unknown Sources")
                    }
                    H2 {
                        Text("2. Get the app")
                    }
                    A("/ailaai.apk", {
                        style {
                            display(DisplayStyle.InlineBlock)
                            padding(1.cssRem, 2.cssRem)
                            fontWeight(700)
                            fontSize(18.px)
                            borderRadius(4.cssRem)
                            color(Color.white)
                            textDecoration("none")
                            property("box-shadow", "2px 2px 8px rgba(0, 0, 0, .25)")
                            backgroundColor(Styles.colors.primary)
                        }
                    }) {
                        Text("Install Ai Là Ai")
                    }
                }
            }
        }
    }
}

@OptIn(InternalSerializationApi::class)
private fun Card.getConversation() = DefaultJson.decodeFromString(
    ConversationItem::class.serializer(),
    conversation ?: "{}"
)
