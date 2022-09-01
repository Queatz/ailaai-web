import org.jetbrains.compose.web.css.*

object Styles : StyleSheet() {
    object colors {
        val background = Color("#E0F3FF")
        val primary = Color("#006689")
    }

    val mainContent by style {
        media(mediaMaxWidth(640.px)) {
            self style {
                flexDirection(FlexDirection.Column)
            }
        }
    }

    val navContent by style {
        media(mediaMaxWidth(640.px)) {
            self style {
                flexShrink(0)
                maxHeight(50.vh)
            }
        }
        media(mediaMinWidth(641.px)) {
            self style {
                width(240.px)
                minWidth(240.px)
                overflowY("auto")
            }
        }
    }

    val card by style {
        display(DisplayStyle.Flex)
        borderRadius(PaddingDefault)
        backgroundColor(colors.background)
        margin(PaddingDefault / 2)
        width(240.px)
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
