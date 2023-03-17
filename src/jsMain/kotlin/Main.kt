import androidx.compose.runtime.*
import app.softwork.routingcompose.BrowserRouter
import app.softwork.routingcompose.Router
import components.CardPage
import io.ktor.client.*
import io.ktor.client.engine.js.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.serialization.Serializable
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.renderComposableInBody

const val baseUrl = "https://api.ailaai.app"

val http = HttpClient(Js) {
    expectSuccess = true
    install(ContentNegotiation) { json() }
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

        var card by remember { mutableStateOf<Card?>(null) }

        LaunchedEffect(card?.id) {
            document.title = card?.name ?: "Ai Là Ai"
        }

        BrowserRouter("") {
            val router = Router.current

            LaunchedEffect(router.currentPath) {
                window.scrollTo(0.0, 0.0)
                document.title = "Ai Là Ai"
            }

            route("card") {
                string { cardId ->
                    CardPage(cardId) {
                        card = it
                    }
                }
            }

            noMatch {
                Div({
                    style {
                        property("margin", "$PaddingDefault auto")
                        maxWidth(800.px)
                        padding(1.5.cssRem)
                        fontSize(22.px)
                        lineHeight("1.5")
                    }
                }) {
                    Div({
                        style {
                            textAlign("center")
                            marginBottom(2.cssRem)
                        }
                    }) {
                        Img("/icon.png")
                    }
                    B {
                        Text("Ai Là Ai")
                    }
                    Text(" is a card-sharing game that makes your offline world more interactive and fun. You will make tons of new connections and get to know the other players.")
                    Br()
                    Br()
                    Text(" To play, you'll need an invite from another player. If you're new here, ")
                    A("mailto:jacobaferrero@gmail.com?subject=Ai Là Ai invite to play") {
                        Text("send me an email")
                    }
                    Text(" and play today!")
                    H3 {
                        Text("How to play")
                    }
                    Ul {
                        Li { Text("Open the app in different locations to collect cards from other players") }
                        Li { Text("Place cards on the ground, equip them, stack them on other cards, or put them in the real world using NFC Tags and QR Codes") }
                        Li { Text("Wait for other players to find your cards and reply") }
                        Li { Text("Reply to a card to start a conversation with the other player") }
                    }
                    H3 {
                        Text("How to win")
                    }
                    Ul {
                        Li { Text("You win when you play with your offline world more than with your phone.") }
                    }
                    H3 {
                        Text("Get the app")
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
                        Text("Download Ai Là Ai")
                    }
                }
            }
        }
    }
}
