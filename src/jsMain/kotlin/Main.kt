import androidx.compose.runtime.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.js.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jetbrains.compose.web.css.*
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

fun main() {
    renderComposableInBody {
        var card by remember { mutableStateOf<Card?>(null) }

//        BrowserRouter("/card") {
//            route("/card") {
//                int { cardId ->

        LaunchedEffect(true) {
            card = http.get("$baseUrl/cards/50502555").body()
        }

        Div({
            style {
                display(DisplayStyle.Flex)
                height(100.percent)
                width(100.percent)
                padding(PaddingDefault)
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
                    backgroundColor(Color.aliceblue)
                    overflow("hidden")
                }
            }) {
                Text(card?.name ?: "Loading...")
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

//                    }
//                }
//            }
//        }

    }
}
