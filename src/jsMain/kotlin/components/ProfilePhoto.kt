package components

import Person
import androidx.compose.runtime.Composable
import baseUrl
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.dom.Text

@Composable
fun ProfilePhoto(person: Person, size: CSSNumeric = 36.px, onClick: (() -> Unit)? = null, styles: (StyleScope.() -> Unit)? = null) {
    if (person.photo == null) {
        Div({
            classes(Styles.profilePhotoText)

            style {
                width(size)
                height(size)

                if (onClick != null) {
                    cursor("pointer")
                }
                styles?.invoke(this)
            }

            title(person.name ?: "Someone")

            onClick {
                onClick?.invoke()
            }
        }) {
            Text(person.name?.take(1) ?: "?")
        }
    } else {
        Div({
            style {
                width(size)
                height(size)
                borderRadius(100.percent)
                backgroundColor(Styles.colors.background)
                backgroundPosition("center")
                backgroundSize("cover")
                backgroundImage("url('$baseUrl${person.photo}')")
                if (onClick != null) {
                    cursor("pointer")
                }
                styles?.invoke(this)
            }

            title(person.name ?: "Someone")

            onClick {
                onClick?.invoke()
            }
        })
    }
}
