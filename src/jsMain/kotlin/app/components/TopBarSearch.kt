package app.components

import androidx.compose.runtime.Composable
import appString
import components.SearchField
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import r

@Composable
fun TopBarSearch(value: String, onValue: (String) -> Unit) {
    Div({
        style {
            display(DisplayStyle.Flex)
            flexDirection(FlexDirection.Column)
            maxWidth(800.px)
            width(100.percent)
            alignSelf(AlignSelf.Center)
        }
    }) {
        SearchField(
            value,
            appString { this.search },
            {
                margin(1.r)
            }
        ) {
            onValue(it)
        }
    }
}