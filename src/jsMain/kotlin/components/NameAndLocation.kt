package components

import PaddingDefault
import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

@Composable
fun NameAndLocation(name: String?, location: String?) {
    Span({
        style {
            fontWeight("bold")
            fontSize(24.px)
        }
    }) {
        Text(name ?: "")
    }
    Span({
        style {
            marginLeft(PaddingDefault / 2)
            fontSize(18.px)
            opacity(.75f)
        }
    }) {
        Text(location ?: "")
    }
}
