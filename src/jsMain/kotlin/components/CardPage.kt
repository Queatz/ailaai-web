package components

import Card
import CardNameAndLocation
import ConversationItem
import CornerDefault
import PaddingDefault
import androidx.compose.runtime.*
import app.softwork.routingcompose.Router
import baseUrl
import http
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.decodeFromString
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text

@Composable
fun CardPage(cardId: String, cardLoaded: (card: Card) -> Unit) {
    var isLoading by remember { mutableStateOf(false) }

    var card by remember { mutableStateOf<Card?>(null) }
    var cards by remember { mutableStateOf<List<Card>>(emptyList()) }
    val stack = remember { mutableListOf<ConversationItem>() }
    var cardConversation by remember { mutableStateOf<ConversationItem?>(null) }

    LaunchedEffect(cardId) {
        isLoading = true
        try {
            card = http.get("$baseUrl/cards/$cardId").body()
            cardConversation = card!!.getConversation()
            cardLoaded(card!!)
            val ktorHackfix = http.get("$baseUrl/cards/$cardId/cards").bodyAsText()
            cards = DefaultJson.decodeFromString(ktorHackfix)
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
        }) {
            Div({
                classes(Styles.navContainer)
            }) {
                Div({
                    classes(Styles.navContent)
                    style {
                        width(100.percent)
                        maxWidth(1200.px)
                    }
                }) {
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
                    justifyContent(JustifyContent.Center)
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
                        onClick {
                            router.navigate("/card/${card.id}")
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
