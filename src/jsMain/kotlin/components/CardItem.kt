package components

import Card
import CornerDefault
import PaddingDefault
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import app.softwork.routingcompose.Router
import baseUrl
import kotlinx.browser.window
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Source
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.dom.Video

@Composable
fun CardItem(card: Card, router: Router) {
    val scope = rememberCoroutineScope()
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
        card.video?.let {
        Video({
//            attr("autoplay", "")
            attr("loop", "")
//            attr("muted", "")
            attr("playsinline", "")
//            attr("onloadedmetadata", "this.muted=true")
            style {
                property("object-fit", "cover")
                position(Position.Absolute)
                top(0.px)
                bottom(0.px)
                left(0.px)
                right(0.px)
                property("z-index", "0")
                backgroundColor(Styles.colors.background)
                property("aspect-ratio", "2")
            }
        }) {
            Source({
                attr("src", "$baseUrl$it")
                attr("type", "video/webm")
            })
        }
    }
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
                    property("z-index", "1")
                }
            }) {
                Text("Tap to open${if (numberOfCards > 0) " • $numberOfCards ${if (numberOfCards == 1) "card" else "cards"}" else ""}")
            }
        }
        Div({
            classes(Styles.cardPost)
            style {
                property("z-index", "1")
            }
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
