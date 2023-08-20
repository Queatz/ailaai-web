package app

import CornerDefault
import androidx.compose.runtime.Composable
import baseUrl
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div

@Composable
fun StickerItem(photo: String, size: CSSNumeric = 54.px, title: String? = null, onClick: () -> Unit) {
    Div({
        style {
            width(size)
            height(size)
            borderRadius(CornerDefault / 2)
            backgroundImage("url('$baseUrl$photo')")
            backgroundRepeat("no-repeat")
            backgroundSize("contain")
            backgroundPosition("center")
            marginRight(.5.cssRem)
            cursor("pointer")
            flexShrink(0)
        }
        if (title != null) {
            title(title)
        }
        onClick {
            onClick()
        }
    })
}
