package components

import Styles
import androidx.compose.runtime.Composable
import app.softwork.routingcompose.Router
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div

@Composable fun InfoPage(page: String) {
    Div({
        classes(Styles.mainContent)
    }) {
        Div({
            classes(Styles.navContainer)
            style {
                width(1200.px)
                flexShrink(1f)
                alignSelf(AlignSelf.Center)
                marginBottom(1.cssRem)
            }
        }) {
            Div({
                classes(Styles.navContent)
                style {
                    padding(1.cssRem)
                }
            }) {
                when (page) {
                    "contact" -> {
                        ContactPage()
                    }
                    else -> Router.current.navigate("/")
                }
            }
        }
    }
}
