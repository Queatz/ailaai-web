package components

import PaddingDefault
import Styles
import androidx.compose.runtime.Composable
import app.softwork.routingcompose.Router
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

@Composable
fun AppHeader(title: String) {
    val router = Router.current

    Div({
        classes(Styles.appHeader)
    }) {
        Img("/icon.png") {
            style {
                width(54.px)
                height(54.px)
                cursor("pointer")
            }
            onClick {
                router.navigate("/")
            }
        }
        Span({
            style {
                paddingLeft(PaddingDefault)
                fontSize(24.px)
            }
        }) {
            Text(title)
        }
        Span({
            style {
                flexGrow(1f)
            }
        }) {
            Text("")
        }
        Span({
            classes("material-symbols-outlined")
            style {
                fontSize(32.px)
                paddingRight(PaddingDefault / 2)
                cursor("pointer")
            }
            onClick {
                router.navigate("/")
            }
        }) {
            Text("download")
        }
    }
}
