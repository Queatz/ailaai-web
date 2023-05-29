import org.jetbrains.compose.web.css.*

object Styles : StyleSheet() {
    object colors {
        val background = Color("#E0F3FF")
        val primary = Color("#006689")
    }

    init {
        "a" style {
            color(colors.primary)
            fontWeight("bold")
            textDecoration("none")
        }
    }

    val appHeader by style {
        boxSizing("border-box")
        display(DisplayStyle.Flex)
        alignItems(AlignItems.Center)
        padding(PaddingDefault)
        marginTop(PaddingDefault)
        marginLeft(PaddingDefault)
        marginRight(PaddingDefault)
        marginBottom(PaddingDefault * 2)
        property("box-shadow", "rgba(0, 0, 0, 0.125) 1px 1px 4px")
        backgroundColor(Color.white)
        borderRadius(CornerDefault)

    }

    val mainContent by style {
        boxSizing("border-box")
        display(DisplayStyle.Flex)
        flexDirection(FlexDirection.Column)
        minHeight(100.vh)
        alignItems(AlignItems.Center)
        justifyContent(JustifyContent.Stretch)
    }

    val navContainer by style {
        boxSizing("border-box")
        display(DisplayStyle.Flex)
        flexDirection(FlexDirection.Column)
        alignItems(AlignItems.Center)
        padding(PaddingDefault)
        overflowX("hidden")
        property("box-shadow", "rgba(0, 0, 0, 0.125) 2px 2px 16px")
        backgroundColor(Color.white)
        borderRadius(CornerDefault)
        width(1200.px)
        marginLeft(PaddingDefault)
        marginRight(PaddingDefault)
        property("max-width", "calc(100vw - ${PaddingDefault * 2})")
    }

    val navContent by style {
        display(DisplayStyle.Flex)
        flexDirection(FlexDirection.Column)
        alignItems(AlignItems.FlexStart)
        width(100.percent)
        maxWidth(1200.px)
        boxSizing("border-box")

        not(lastChild) style {
            marginBottom(PaddingDefault)
        }

        media(mediaMinWidth(641.px)) {
            self style {
                padding(PaddingDefault)
            }
        }
    }

    val content by style {
        display(DisplayStyle.Flex)
        flexDirection(FlexDirection.Row)
        flexWrap(FlexWrap.Wrap)
        flexGrow(1)
        padding(PaddingDefault)
        overflow("auto")
        padding(PaddingDefault / 2)
        justifyContent(JustifyContent.Center)
        alignContent(AlignContent.FlexStart)

        media(mediaMinWidth(641.px)) {
            self style {
                padding(PaddingDefault)
            }
        }
    }

    val card by style {
        display(DisplayStyle.Flex)
        borderRadius(PaddingDefault)
        backgroundColor(colors.background)
        margin(PaddingDefault)
        width(640.px)
        overflow("hidden")
        flexDirection(FlexDirection.ColumnReverse)
        cursor("pointer")
        property("aspect-ratio", ".75")
        property("box-shadow", "2px 2px 8px rgba(0, 0, 0, .25)")

        media(mediaMaxWidth(640.px)) {
            self style {
                width(100.percent)
                margin(PaddingDefault / 2)
            }
        }
    }

    val button by style {
        borderRadius(PaddingDefault * 2)
        border(0.px)
        padding(0.cssRem, PaddingDefault * 2)
        height(3.cssRem)
        backgroundColor(colors.primary)
        color(Color.white)
        cursor("pointer")
        display(DisplayStyle.Flex)
        alignItems(AlignItems.Center)

        selector(".material-symbols-outlined") style {
            marginRight(.5.cssRem)
        }
    }

    val outlineButton by style {
        borderRadius(PaddingDefault * 2)
        border(1.px, LineStyle.Solid, colors.primary)
        padding(0.cssRem, PaddingDefault * 2)
        height(3.cssRem)
        backgroundColor(Color.transparent)
        color(colors.primary)
        cursor("pointer")
        display(DisplayStyle.Flex)
        alignItems(AlignItems.Center)

        selector(".material-symbols-outlined") style {
            marginRight(.5.cssRem)
        }
    }

    val textButton by style {
        property("border", "none")
        padding(0.cssRem)
        height(3.cssRem)
        backgroundColor(Color.transparent)
        color(colors.primary)
        cursor("pointer")
        display(DisplayStyle.Flex)
        alignItems(AlignItems.Center)
        fontWeight(500)
        property("font-size", "inherit")
        selector(".material-symbols-outlined") style {
            marginRight(.5.cssRem)
        }
    }
}
