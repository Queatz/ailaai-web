package app.page

import Styles.card
import org.jetbrains.compose.web.css.*


object CardsPageStyles : StyleSheet() {
    val layout by style {
        boxSizing("border-box")
        display(DisplayStyle.Flex)
        flexWrap(FlexWrap.Wrap)
        position(Position.Relative)

        child(self, className(card)) style {
            self style {
                width(320.px)
                marginTop(1.cssRem)
                marginLeft(1.cssRem)
            }
        }
    }
}
