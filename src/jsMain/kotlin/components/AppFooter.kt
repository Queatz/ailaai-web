package components

import androidx.compose.runtime.Composable
import appString
import org.jetbrains.compose.web.css.Color
import org.jetbrains.compose.web.css.color
import org.jetbrains.compose.web.css.whiteSpace
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

@Composable
fun AppFooter() {
    Div({
        classes(Styles.appFooter)
        style {
            whiteSpace("pre-wrap")
        }
    }) {
        Span { Text(appString { madeWith }) }
        Span({
            style { color(Color.red) }
        }) { Text(" ♥ ") }
        Span { Text(appString { inHCMC }) }
    }
}
