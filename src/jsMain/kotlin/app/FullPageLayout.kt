package app

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div

@Composable
fun FullPageLayout(content: @Composable () -> Unit) {
    Div({
        style {
            display(DisplayStyle.Flex)
            flexDirection(FlexDirection.Column)
            width(100.percent)
            height(100.percent)
            overflowX("hidden")
            overflowY("auto")
        }
    }) {
        Div({
            style {
                display(DisplayStyle.Flex)
                flexDirection(FlexDirection.Column)
                width(100.percent)
                height(100.percent)
                alignItems(AlignItems.Stretch)
                maxWidth(960.px)
                alignSelf(AlignSelf.Center)
            }
        }) {
            content()
        }
    }
}
