package components

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.css.StyleScope
import org.jetbrains.compose.web.css.cursor
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

@Composable
fun IconButton(name: String, title: String, styles: (StyleScope.() -> Unit)? = null, onClick: () -> Unit) {
    Span({
        classes("material-symbols-outlined")
        title(title)
        style {
            cursor("pointer")
            styles?.invoke(this)
        }
        onClick {
            onClick()
        }
    }) {
        Text(name)
    }
}
