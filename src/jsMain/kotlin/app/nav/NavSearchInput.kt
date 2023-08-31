package app.nav

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.attributes.autoFocus
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.margin
import org.jetbrains.compose.web.dom.TextInput

@Composable
fun NavSearchInput(value: String, onChange: (String) -> Unit, placeholder: String = "Search", autoFocus: Boolean = true, onDismissRequest: () -> Unit = {}, onSubmit: (String) -> Unit = {}) {
    TextInput(value) {
        classes(Styles.textarea)
        style {
            margin(.5.cssRem, 1.cssRem, 0.cssRem, 1.cssRem)
        }
        onKeyDown {
            if (it.key == "Escape" || (it.key == "Backspace" && value.isEmpty())) {
                it.preventDefault()
                it.stopPropagation()
                onDismissRequest()
            } else if (it.key == "Enter") {
                onSubmit(value)
            }
        }

        onInput {
            onChange(it.value)
        }

        placeholder(placeholder)

        if (autoFocus) {
            autoFocus()

            ref { element ->
                element.focus()
                onDispose {}
            }
        }
    }
}
