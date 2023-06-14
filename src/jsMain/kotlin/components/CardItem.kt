package components

import Card
import CornerDefault
import PaddingDefault
import androidx.compose.runtime.Composable
import app.softwork.routingcompose.Router
import baseUrl
import kotlinx.browser.window
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text

@Composable
fun CardItem(card: Card, router: Router) {
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
        (card.cardCount?.takeIf { it > 0 } ?: 0).let { numberOfCards ->
            Div({
                style {
                    backgroundColor(rgba(255, 255, 255, .95))
                    borderRadius(CornerDefault * 2)
                    padding(PaddingDefault / 2, PaddingDefault)
                    color(Color.black)
                    position(Position.Absolute)
                    top(PaddingDefault)
                    right(PaddingDefault)
                }
            }) {
                Text("Tap to open${if (numberOfCards > 0) " • $numberOfCards ${if (numberOfCards == 1) "card" else "cards"}" else ""}")
            }
        }
        Div({
            classes(Styles.cardPost)
        }) {
            Div({
                style {
                    marginBottom(PaddingDefault)
                }
            }) {
                NameAndLocation(card.name, card.location)
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
