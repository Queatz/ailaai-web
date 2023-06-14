package profile

import PaddingDefault
import Styles
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.Color.white

object ProfileStyles : StyleSheet() {
    val mainContent by style {
        display(DisplayStyle.Flex)
        media(mediaMaxWidth(640.px)) {
            self style {
                flexDirection(FlexDirection.Column)
                alignItems(AlignItems.Center)
            }
        }
    }
    val photo by style {
        margin(PaddingDefault, 0.cssRem, PaddingDefault, PaddingDefault)
        width(256.px)
        height(256.px)
        maxWidth(33.333.vw)
        maxHeight(33.333.vw)
        backgroundColor(Styles.colors.background)
        backgroundPosition("center")
        backgroundSize("cover")
        borderRadius(100.percent)
        border(6.px, LineStyle.Solid, Color.white)
        media(mediaMaxWidth(640.px)) {
            property("margin-top", "-22.5vw")
        }
        media(mediaMinWidth(641.px)) {
            self style {
                property("transform", "translateY(calc(-128px - -1rem))")
                margin(PaddingDefault * 1.5f, 0.cssRem, PaddingDefault * 1.5f, PaddingDefault * 1.5f)
            }
        }
    }

    val profileContent by style {
        media(mediaMinWidth(641.px)) {
            self style {
                flexGrow(1)
                flexShrink(1)
                width(0.px)
            }
        }
    }

    val infoCard by style {
        padding(1.cssRem)
        border(1.px, LineStyle.Solid, Styles.colors.background)
        borderRadius(1.cssRem)
        overflow("hidden")
        display(DisplayStyle.Flex)
        flexDirection(FlexDirection.Column)
        alignItems(AlignItems.Stretch)

        child(self, selector("div")) style {
            overflow("hidden")
            whiteSpace("nowrap")
            property("text-overflow", "ellipsis")
            textAlign("center")
        }

        self + not(lastChild) style {
            marginRight(1.cssRem)
        }

        media(mediaMaxWidth(640.px)) {
            self style {
                flex(1)
                width(0.px)
            }
        }

        media(mediaMinWidth(641.px)) {
            self style {
                width(4.cssRem)
            }
        }
    }

    val infoCardName by style {
        color(Styles.colors.secondary)
    }
}
