package app.nav

import Person
import androidx.compose.runtime.Composable
import appString
import components.ProfilePhoto
import kotlinx.browser.window
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.ElementScope
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLDivElement

@Composable
fun NavTopBar(me: Person?, title: String, onProfileClick: () -> Unit, actions: @Composable ElementScope<HTMLDivElement>.() -> Unit = {}) {
    Div({
        style {
            display(DisplayStyle.Flex)
            justifyContent(JustifyContent.FlexEnd)
            alignItems(AlignItems.Center)
            padding(1.cssRem, 1.cssRem, .5.cssRem, 1.cssRem)
        }
    }) {
        Div({
            style {
                flex(1)
                marginRight(1.cssRem)
                fontSize(24.px)
            }
        }) {
            Text(title)
        }
        actions()
        me?.let { me ->
            ProfilePhoto(me, title = appString { profile }, onClick = {
                onProfileClick()
            })
        }
    }
}
