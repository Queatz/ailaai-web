package components

import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import kotlin.time.Duration.Companion.seconds

@Composable
fun Loading() {
    var opacity by remember {
        mutableStateOf(0f)
    }
    Div({
        style {
            height(100.percent)
            display(DisplayStyle.Flex)
            alignItems(AlignItems.Center)
            justifyContent(JustifyContent.Center)
            opacity(opacity)
        }
    }) {
        var side by remember {
            mutableStateOf("")
        }

        LaunchedEffect(Unit) {
            while (true) {
                delay(.5.seconds)
                opacity = .25f
                side = when (side) {
                    "_top" -> "_bottom"
                    "_bottom" -> ""
                    else -> "_top"
                }
            }
        }

        Span({
            classes("material-symbols-outlined")
        }) {
            Text("hourglass$side")
        }
    }
}
