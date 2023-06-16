import androidx.compose.runtime.*
import app.softwork.routingcompose.BrowserRouter
import app.softwork.routingcompose.Router
import components.AppHeader
import components.CardPage
import components.ProfilePage
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

fun main() {
    renderComposableInBody {
        Style(Styles)

        var title by remember { mutableStateOf<String?>(null) }
        var parentCardId by remember { mutableStateOf<String?>(null) }

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
                    AppHeader("Explore", showBack = parentCardId != null, onBack = {
                        router.navigate("/card/$parentCardId")
                    })
                    CardPage(cardId, onError = { parentCardId = null }) {
                        title = it.name
                        parentCardId = it.parent
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

            route("profile") {
                string { profileUrl ->
                    AppHeader("Ai là ai")
                    ProfilePage(profileUrl) {
                        title = it.person.name ?: "Someone"
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
                            Text("Coalesce your city's social scene.")
                        }
                    }
                    H3 {
                        Text("What is Ai là ai?")
                    }
                    Div {
                        Text("Ai là ai is a platform that lets you discover and integrate into your city's social scene in meaningful and useful ways to you.")
                        Br()
                        Br()
                    }
                    Div {
                        Text(" To join the Beta, you ")
                        Span(
                            {
                                style {
                                    textDecoration("underline")
                                }
                            }
                        ) { Text("need an invite") }
                        Text(" from another member. If you're new here, ")
                        A("mailto:jacobaferrero@gmail.com?subject=Ai là ai invite to join") {
                            Text("send me an email")
                        }
                        Text(" and start engaging your city today!")
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
                        Text(" Download Ai là ai Beta for Android")
                    }
                }
            }
        }
    }
}
