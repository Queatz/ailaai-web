package components

import PaddingDefault
import Styles
import androidx.compose.runtime.Composable
import app.softwork.routingcompose.Router
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.Color.white
import org.jetbrains.compose.web.dom.*

@Composable
fun AppHeader(
    title: String,
    showBack: Boolean = false,
    onBack: () -> Unit = {}
) {
    val router = Router.current

    Div({
        classes(Styles.appHeader)
    }) {
        if (showBack) {
            Button({
                classes(Styles.textButton)
                style {
                    marginLeft(.5.cssRem)
                }
                onClick {
                    onBack()
                }
            }) {
                Span({
                    classes("material-symbols-outlined")
                }) {
                    Text("arrow_back")
                }
                Text(" Go back")
            }
        } else {
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
                    overflow("hidden")
                    whiteSpace("nowrap")
                    property("text-overflow", "ellipsis")
                    marginRight(.5.cssRem)
                }
            }) {
                Text(title)
            }
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
