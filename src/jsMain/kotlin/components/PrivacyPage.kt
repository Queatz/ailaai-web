package components

import PaddingDefault
import androidx.compose.runtime.Composable
import appText
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H3
import org.jetbrains.compose.web.dom.Text

@Composable
fun PrivacyPage() {
    Div({
        style {
            property("margin", "$PaddingDefault auto")
            maxWidth(1200.px)
            padding(0.cssRem, 1.cssRem, 1.cssRem, 1.cssRem)
            fontSize(22.px)
            lineHeight("1.5")
            minHeight(100.vh)
        }
    }) {
        H3 {
            appText { privacyPolicy }
        }
        Div {
            Text("Data created by you that is not made publicly available on the Platform is not accessed or provided to any other entity except when required by law and to resolve reported content.")
        }
    }
}
