package app.menu

import androidx.compose.runtime.Composable
import app.AppStyles
import focusable
import kotlinx.browser.document
import kotlinx.browser.window
import org.jetbrains.compose.web.css.left
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.top
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.DOMRect
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.EventListener
import org.w3c.dom.events.MouseEvent
import parents

class MenuScope {
    @Composable
    fun item(title: String, onClick: () -> Unit) {
        Div({
            classes(AppStyles.menuItem)

            focusable()

            onClick {
                onClick()
            }
        }) {
            Text(title)
        }
    }
}

@Composable
fun Menu(
    onDismissRequest: () -> Unit,
    target: DOMRect,
    content: @Composable MenuScope.() -> Unit
) {
    Div({
        classes(AppStyles.menu)

        style {
            target.let { it.left + it.width to it.top + it.height }
                .let { (left, top) ->
                    top(top.px)
                    left(left.px)
                }
        }

        onClick {
            onDismissRequest()
        }

        ref { menuElement ->
            val clickListener = EventListener {
                val parents = ((it as? MouseEvent)?.target as? HTMLElement)?.parents
                if (parents?.none { it == menuElement } == true) {
                    onDismissRequest()
                }
            }
            val resizeListener = EventListener { onDismissRequest() }
            document.addEventListener("click", clickListener)
            window.addEventListener("resize", resizeListener)

            onDispose {
                document.removeEventListener("click", clickListener)
                window.removeEventListener("resize", resizeListener)
            }
        }
    }) {
        MenuScope().content()
    }
}

