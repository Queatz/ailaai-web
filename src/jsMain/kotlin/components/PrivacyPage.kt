package components

import PaddingDefault
import androidx.compose.runtime.Composable
import appText
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H3

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
            appText { privacyPolicyText }
        }
    }
}
