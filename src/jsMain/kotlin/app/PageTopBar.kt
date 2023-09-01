package app

import androidx.compose.runtime.Composable
import androidx.compose.web.events.SyntheticMouseEvent
import components.IconButton
import ellipsize
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text

@Composable
fun PageTopBar(title: String, description: String? = null, onMenu: ((SyntheticMouseEvent) -> Unit)? = null) {
    Div({
        style {
            display(DisplayStyle.Flex)
            padding(.5.cssRem)
            margin(1.cssRem, 1.cssRem, .5.cssRem, 1.cssRem)
            alignItems(AlignItems.Center)
            overflow("hidden")
            flexShrink(0)
        }
    }) {
        Div({
            style {
                display(DisplayStyle.Flex)
                flexDirection(FlexDirection.Column)
                flex(1)
                overflow("hidden")
            }
        }) {
            Div({
                style {
                    fontSize(24.px)
                    ellipsize()
                }
            }) {
                Text(title)
            }
            if (!description.isNullOrBlank()) {
                Div({
                    classes(AppStyles.groupItemMessage)
                    style {
                        ellipsize()
                    }
                }) {
                    Text(description)
                }
            }
        }
        IconButton("more_vert", "Options", styles = {
            flexShrink(0)
            fontWeight("bold")
        }) {
            onMenu?.invoke(it)
        }
    }
}
