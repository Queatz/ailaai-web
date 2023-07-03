package components

import PaddingDefault
import androidx.compose.runtime.*
import app.softwork.routingcompose.Router
import appString
import cities.CityStyles
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import kotlin.random.Random

@Composable
fun CitiesPage() {
    Style(CityStyles)

    val router = Router.current

    Div({
        style {
            property("margin", "$PaddingDefault auto")
            maxWidth(1200.px)
            padding(0.cssRem, 1.cssRem, 1.cssRem, 1.cssRem)
            fontSize(22.px)
            lineHeight("1.5")
            minHeight(100.vh)
        }
    }) {
        var searchText by remember { mutableStateOf("") }
        SearchField(searchText, appString { search }, modifier = {
            marginTop(1.cssRem)
            marginBottom(1.cssRem)
        }) {
            searchText = it
        }
        Div({
            classes(CityStyles.cities)
        }) {
            listOf(
                "Hồ Chí Minh, Việt Nam" to "/photos/saigon.jpg",
            ).filter {
                searchText.isBlank() || it.first.lowercase().contains(searchText.lowercase())
            }.forEach {
                Div({
                    classes(CityStyles.city)
                    style {
                        backgroundImage("url(${it.second})")
                        position(Position.Relative)
                    }
                    onClick {
                        router.navigate("/")
                    }
                }) {
                    Span({
                        classes(CityStyles.gradient)
                    }) {  }
                    Div({
                        style {
//                            borderRadius(1.cssRem)
//                            background("rgba(0, 0, 0, .333)")
                            padding(1.cssRem)
                            property("z-index", "1")
                        }
                    }) {
                        Div { Text(it.first) }
                        Div({
                            style {
                                fontSize(24.px)
                            }
                            classes(CityStyles.cityAbout)
                        }) {
                            Text("Một nơi của mọi thứ, mọi người và tất cả những gì đã và đang tồn tại. Một khát vọng của thế giới nói chung.")
                        }
                        Div({
                            style {
                                fontSize(16.px)
                                opacity(.75f)
                                marginTop(.5.cssRem)
                                color(Styles.colors.tertiary)
                            }
                        }) {
                            Text("Bây giờ có 2 người ở đay")
                        }
                    }
                }
            }
        }
    }
}
