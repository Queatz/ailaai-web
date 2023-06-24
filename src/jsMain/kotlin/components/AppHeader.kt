package components

import LocalConfiguration
import PaddingDefault
import Strings
import Styles
import androidx.compose.runtime.Composable
import app.softwork.routingcompose.Router
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import appString

@Composable
fun AppHeader(
    title: String,
    showBack: Boolean = false,
    showDownload: Boolean = true,
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
                Text(" ${appString { goBack }}")
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
        val configuration = LocalConfiguration.current
        Span({
            style {
                padding(.5.cssRem, 1.cssRem)
                fontSize(32.px)
                if (showDownload) {
                    marginRight(.5.cssRem)
                }
                property("user-select", "none")
                cursor("pointer")
            }
            onClick {
                configuration.set(
                    when (configuration.language) {
                        "en" -> "vi"
                        "vi" -> "ru"
                        else -> "en"
                    }
                )
            }
            title(
                when (configuration.language) {
                    "vi" -> "Language"
                    "ru" -> "Язык"
                    else -> "Ngôn ngữ"
                }
            )
        }) {
            when (configuration.language) {
                "vi" -> Text("\uD83C\uDDFB\uD83C\uDDF3")
                "ru" -> Text("\uD83C\uDDF7\uD83C\uDDFA")
                else -> Text("\uD83C\uDDEC\uD83C\uDDE7")
            }
        }
        if (showDownload) {
            Span({
                classes("material-symbols-outlined")
                style {
                    fontSize(32.px)
                    paddingRight(PaddingDefault / 2)
                    property("user-select", "none")
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
}
