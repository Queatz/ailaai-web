package app.nav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import application
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text

@Composable
fun CardsNavPage() {
    val me by application.me.collectAsState()
    NavTopBar(me, "Cards")

    Div({
        style {
            height(100.percent)
            display(DisplayStyle.Flex)
            alignItems(AlignItems.Center)
            justifyContent(JustifyContent.Center)
            opacity(.5)
        }
    }) {
        Text("Card tools will be here")
    }
}
