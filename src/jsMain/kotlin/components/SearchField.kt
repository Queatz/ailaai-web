package components

import androidx.compose.runtime.Composable
import appString
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.autoFocus
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

@Composable
fun SearchField(
    value: String,
    placeholder: String,
    modifier: StyleScope.() -> Unit = {},
    valueChange: (String) -> Unit
) {
    Div({
        style {
            position(Position.Relative)
            modifier()
        }
    }) {
        Input(InputType.Text) {
            classes(Styles.textarea)
            style {
                width(100.percent)
                paddingLeft(3.cssRem)
            }

            if (value.isNotEmpty()) {
                defaultValue(value)
            }

            placeholder(placeholder)

            onInput {
                valueChange(it.value)
            }

            autoFocus()
        }
        Span({
            classes("material-symbols-outlined")
            style {
                position(Position.Absolute)
                property("z-index", "1")
                property("pointer-events", "none")
                color(Styles.colors.primary)
                left(1.cssRem)
                top(0.cssRem)
                bottom(0.cssRem)
                display(DisplayStyle.Flex)
                alignItems(AlignItems.Center)
            }
        }) {
            Text("search")
        }
    }
}
