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
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposableInBody

const val baseUrl = "http://localhost:8080"

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

@OptIn(InternalSerializationApi::class)
fun main() {
    renderComposableInBody {
        var card by remember { mutableStateOf<Card?>(null) }
        var cardConversation by remember { mutableStateOf<ConversationItem?>(null) }

        BrowserRouter("/card") {
            route("/card") {
                string { cardId ->
                    LaunchedEffect(true) {
                        card = http.get("$baseUrl/cards/$cardId").body()
                        cardConversation = DefaultJson.decodeFromString(ConversationItem::class.serializer(), card?.conversation ?: "{}")
                    }

                    Div({
                        style {
                            display(DisplayStyle.Flex)
                            height(100.percent)
                            width(100.percent)
                            alignItems(AlignItems.Stretch)
                            justifyContent(JustifyContent.Stretch)
                        }
                    }) {
                        Div({
                            style {
                                display(DisplayStyle.Flex)
                                flexDirection(FlexDirection.Column)
                                padding(PaddingDefault)
                                width(240.px)
                                minWidth(240.px)
                                backgroundColor(Color.aliceblue)
                                overflow("hidden")
                            }
                        }) {
                            card?.photo?.let {
                                Div({
                                    style {
                                        width(100.percent)
                                        height(240.px)
                                        backgroundImage("url($baseUrl${it})")
                                        backgroundPosition("center")
                                        backgroundSize("cover")
                                        borderRadius(CornerDefault)
                                        marginBottom(PaddingDefault)
                                    }
                                }) {}
                            }
                            Div({
                                style {
                                    fontWeight("bold")
                                    marginBottom(PaddingDefault)
                                }
                            }) {
                                Text(card?.name ?: "Loading...")
                            }
                            Div({
                                style {
                                    marginBottom(PaddingDefault)
                                }
                            }) {
                                Text(cardConversation?.message ?: "")
                            }
                        }
                        Div({
                            style {
                                display(DisplayStyle.Flex)
                                flexDirection(FlexDirection.Column)
                                flexGrow(1)
                                padding(PaddingDefault)
                                overflow("hidden")
                            }
                        }) {
                            Text(card?.conversation ?: "")
                        }
                    }
                }
            }

            noMatch {
                val router = Router.current
                Button({
                    onClick {
                        router.navigate("/card/50500361")
                    }
                }) {
                    Text("Go to card 50500361")
                }
            }
        }
    }
}
