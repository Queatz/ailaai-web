import org.jetbrains.compose.web.css.*

object Styles : StyleSheet() {
    object colors {
        val background = Color("#E0F3FF")
        val primary = Color("#006689")
    }

    val mainContent by style {
        boxSizing("border-box")

        media(mediaMaxWidth(640.px)) {
            self style {
                flexDirection(FlexDirection.Column)
            }
        }
    }

    val navContent by style {
        boxSizing("border-box")

        media(mediaMaxWidth(640.px)) {
            self style {
                flexShrink(0)
            }
        }
        media(mediaMinWidth(641.px)) {
            self style {
                width(NavWidth)
                minWidth(NavWidth)
                height(100.vh)
                position(Position.Fixed)
                property("box-shadow", "rgba(0, 0, 0, 0.125) 2px 2px 16px")
            }
        }
        not(lastChild) style {
            marginBottom(PaddingDefault)
        }
    }

    val content by style {
        media(mediaMinWidth(641.px)) {
            self style {
                marginLeft(NavWidth)
            }
        }
    }

    val card by style {
        display(DisplayStyle.Flex)
        borderRadius(PaddingDefault)
        backgroundColor(colors.background)
        margin(PaddingDefault / 2)
        width(640.px)
        overflow("hidden")
        flexDirection(FlexDirection.ColumnReverse)
        cursor("pointer")
        property("aspect-ratio", ".75")
        property("box-shadow", "2px 2px 8px rgba(0, 0, 0, .25)")
    }

    val button by style {
        borderRadius(PaddingDefault * 2)
        border(0.px)
        padding(PaddingDefault, PaddingDefault * 2)
        backgroundColor(colors.primary)
        color(Color.white)
        cursor("pointer")
    }
    val outlineButton by style {
        borderRadius(PaddingDefault * 2)
        border(1.px, LineStyle.Solid, colors.primary)
        padding(PaddingDefault, PaddingDefault * 2)
        backgroundColor(Color.transparent)
        color(colors.primary)
        cursor("pointer")
    }
}
