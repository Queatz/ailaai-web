package components

import Card
import CornerDefault
import PaddingDefault
import Styles
import androidx.compose.runtime.*
import api
import app.softwork.routingcompose.Router
import appString
import baseUrl
import kotlinx.browser.window
import notBlank
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Source
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.dom.Video
import org.w3c.dom.HTMLDivElement
import kotlin.math.roundToInt

@Composable
fun CardItem(cardId: String, openInNewWindow: Boolean = false, styles: (StyleScope.() -> Unit)? = null) {
    var card by remember { mutableStateOf<Card?>(null) }
    LaunchedEffect(Unit) {
        api.card(cardId) {
            card = it
        }
    }
    card?.let { card ->
        CardItem(card, openInNewWindow, styles)
    }
}

@Composable
fun CardItem(
    card: Card,
    openInNewWindow: Boolean = false,
    styles: (StyleScope.() -> Unit)? = null,
    onClick: ((openInNewWindow: Boolean) -> Unit)? = null
) {
    val router = Router.current
    Div({
        classes(Styles.card)
        style {
            if (card.photo != null) {
                backgroundImage("url($baseUrl${card.photo!!})")
                backgroundPosition("center")
                backgroundSize("cover")
            }

            position(Position.Relative)

            styles?.invoke(this)
        }
        onClick { event ->
            if (onClick == null) {
                if (event.ctrlKey || openInNewWindow) {
                    window.open("/card/${card.id}", target = "_blank")
                } else {
                    router.navigate("/card/${card.id}")
                }
            } else {
                onClick(event.ctrlKey || openInNewWindow)
            }
        }
        onMouseMove { event ->
            val amount = 16
            val cardEl = event.currentTarget as HTMLDivElement
            val clientRect = cardEl.getBoundingClientRect()
            val xFactor = (event.clientX - clientRect.x) / clientRect.width.toFloat()
            val yFactor = (event.clientY - clientRect.y) / clientRect.height.toFloat()
            val rotateX = ((xFactor - 0.5f) * amount * 100f).roundToInt().toFloat() / 100f
            val rotateY = ((0.5f - yFactor) * amount * 100f).roundToInt().toFloat() / 100f
            cardEl.style.transform = "perspective(100vw) rotateX(${rotateY}deg) rotateY(${rotateX}deg)"
            cardEl.style.zIndex = "5"
        }
        onMouseOut { event ->
            val cardEl = event.currentTarget as HTMLDivElement
            cardEl.style.transform = "perspective(100vw) rotateX(0deg) rotateY(0deg)"
            cardEl.style.zIndex = ""
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
                Text("${appString { tapToOpen }}${if (numberOfCards > 0) " • $numberOfCards ${if (numberOfCards == 1) appString { inlineCard } else appString { inlineCards }}" else ""}")
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
                card.categories?.firstOrNull()?.let { category ->
                    Div({
                        classes(Styles.category)
                        style {
                            marginBottom(PaddingDefault)
                        }
                    }) {
                        Text(category)
                    }
                }
            }

            card.getConversation().message.notBlank
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
