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
    install(ContentNegotiation) { json(DefaultJson) }
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
                        Text("Download Ai Là Ai")
                    }
                }
            }
        }
    }
}
