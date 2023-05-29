import androidx.compose.runtime.*
import app.softwork.routingcompose.BrowserRouter
import app.softwork.routingcompose.Router
import components.AppHeader
import components.CardPage
import components.StoryPage
import io.ktor.client.*
import io.ktor.client.engine.js.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.renderComposableInBody

const val baseUrl = "https://api.ailaai.app"

val json = Json {
    encodeDefaults = true
    isLenient = true
    allowSpecialFloatingPointValues = true
    ignoreUnknownKeys = true
}


val http = HttpClient(Js) {
    expectSuccess = true
    install(ContentNegotiation) {
        json(json)
    }
}

@Serializable
class Card(
    var person: String? = null,
    var parent: String? = null,
    var name: String? = null,
    var photo: String? = null,
    var location: String? = null,
    var collaborators: List<String>? = null,
    var categories: List<String>? = null,
    var equipped: Boolean? = null,
    var geo: List<Double>? = null,
    var conversation: String? = null,
    var active: Boolean? = null,
    var offline: Boolean? = null,
    var cardCount: Int? = null
) : Model()

@Serializable
class Story(
    var person: String? = null,
    var title: String? = null,
    var url: String? = null,
    var geo: List<Double>? = null,
    var publishDate: String? = null,
    var published: Boolean? = null,
    var content: String? = null,
    var authors: List<Person>? = null
) : Model()

@Serializable
class Person(
    var name: String? = null,
    var photo: String? = null,
    var seen: String? = null
) : Model()

@Serializable
open class Model {
    var id: String? = null
    var createdAt: String? = null
}

fun main() {
    renderComposableInBody {
        Style(Styles)

        var title by remember { mutableStateOf<String?>(null) }

        LaunchedEffect(title) {
            document.title = title ?: "Ai là ai"
        }

        BrowserRouter("") {
            val router = Router.current

            LaunchedEffect(router.currentPath) {
                window.scrollTo(0.0, 0.0)
                document.title = "Ai là ai"
            }

            route("card") {
                string { cardId ->
                    AppHeader("Explore")
                    CardPage(cardId) {
                        title = it.name
                    }
                }
            }

            route("story") {
                string { storyUrl ->
                    AppHeader("Stories")
                    StoryPage(storyUrl) {
                        title = it.title
                    }
                }
            }

            noMatch {
                var defineCity by remember { mutableStateOf(false) }
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
                        H3({
                            style {
                                marginBottom(0.px)
                            }
                        }) {
                            Text("Ai là ai")
                        }
                        Span({
                            style {
                                opacity(0.5f)
                            }
                        }) {
                            Text("A collaborative ")
                            Span ({
                                style {
                                    textDecoration("underline")
                                    cursor("pointer")
                                }
                                onClick {
                                    defineCity = !defineCity
                                }
                            }) {
                                Text("IRL city")
                            }
                            Text(" building game")
                        }
                    }
                    if (defineCity) {
                        Div({
                            style {
                                marginTop(PaddingDefault)
                                marginBottom(PaddingDefault)
                                padding(PaddingDefault)
                                borderRadius(CornerDefault)
                                border(1.px, LineStyle.Solid, Styles.colors.primary)
                            }
                        }) {
                            Text("A city is all the people, animals, nature, places, things, knowledge, activities, services, doings, etc. existing in close proximity with each other.")
                            Br()
                            Br()
                            Text("Whether you choose to build a city in the wilderness, grow a town into a city, or build a city inside another city — it's totally up to you!")
                            Br()
                            Br()
                            A("https://www.wordnik.com/words/city") {
                                Text("More at Wordnik")
                            }
                        }
                    }
                    H3 {
                        Text("How to play")
                    }
                    Span {
                        Text(" To play, you ")
                        Span(
                            {
                                style {
                                    textDecoration("underline")
                                }
                            }
                        ) { Text("need an invite") }
                        Text(" from another player. If you're new here, ")
                        A("mailto:jacobaferrero@gmail.com?subject=Ai là ai invite to play") {
                            Text("send me an email")
                        }
                        Text(" and play today!")
                    }
                    Ol({
                        attr("type", "I")
                    }) {
                        Li {
                            Text("Build your city using ")
                            B {
                                Text("Ai là ai cards")
                            }
                        }
                        Li {
                            Text("Invite people into your city by ")
                            B {
                                Text("publishing stories")
                            }
                        }
                        Li {
                            Text("Engage with people in your city using ")
                            B {
                                Text("groups and messages")
                            }
                        }
                        Li {
                            Text("Play in your city, ")
                            B {
                                Text("offline!")
                            }
                            Text(" You get it, yet?")
                        }
                    }
                    Span {
                        Text("And much more! Why wait? Start building your city today!")
                    }
                    A("/ailaai.apk", {
                        style {
                            display(DisplayStyle.InlineBlock)
                            padding(1.cssRem, 2.cssRem)
                            marginTop(PaddingDefault)
                            fontWeight(700)
                            fontSize(18.px)
                            borderRadius(4.cssRem)
                            color(Color.white)
                            textDecoration("none")
                            property("box-shadow", "2px 2px 8px rgba(0, 0, 0, .25)")
                            backgroundColor(Styles.colors.primary)
                        }
                    }) {
                        Text(" Download Ai là ai for Android")
                    }
                }
            }
        }
    }
}
