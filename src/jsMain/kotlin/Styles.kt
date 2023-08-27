import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*

object Styles : StyleSheet() {
    object colors {
        val background = Color("#E0F3FF")
        val primary = Color("#006689")
        val secondary = Color("#767676")
        val tertiary = Color("#2e8900")

        object dark {
            val background = Color("#18191a")
        }
    }

    fun CSSBuilder.cardStyle() {
        borderRadius(PaddingDefault * 2)
        backgroundColor(colors.background)
        property("box-shadow", "2px 2px 8px rgba(0, 0, 0, .25)")
    }

    fun CSSBuilder.elevated() {
        property("box-shadow", "rgba(0, 0, 0, 0.125) 1px 1px 4px")
        backgroundColor(Color.white)
        borderRadius(CornerDefault)
    }

    init {
        "a" style {
            color(colors.primary)
            fontWeight("bold")
            textDecoration("none")
        }
    }

    val menuButton by style {
        cursor("pointer")

        hover(self) style {
            textDecoration("underline")
        }
    }

    val appHeader by style {
        boxSizing("border-box")
        display(DisplayStyle.Flex)
        alignItems(AlignItems.Center)
        padding(PaddingDefault)
        margin(PaddingDefault)
        elevated()
    }

    val appFooter by style {
        boxSizing("border-box")
        display(DisplayStyle.Flex)
        alignItems(AlignItems.Center)
        justifyContent(JustifyContent.Center)
        padding(PaddingDefault)
        marginTop(PaddingDefault)
        backgroundColor(Color("#f7f7f7"))
    }

    val mainHeader by style {
        display(DisplayStyle.Flex)
        alignItems(AlignItems.Center)
        justifyContent(JustifyContent.Center)
        padding(4.cssRem, 2.cssRem)
        backgroundColor(Color("#2f0729"))
        backgroundImage("url(/saigonnight-mobile.jpg)")
        backgroundPosition("center")
        textAlign("center")
        backgroundSize("cover")
        margin(1.cssRem, 0.cssRem)
        fontSize(32.px)
        color(Color.white)
        borderRadius(PaddingDefault * 2)
        whiteSpace("pre-wrap")
        fontFamily("Estonia")
        lineHeight("1.25")
        property("aspect-ratio", "6/1")
        property("text-shadow", "#fff 0px 0px .5rem")

        media(mediaMinWidth(641.px)) {
            self style {
                fontSize(42.px)
                padding(4.cssRem)
                textAlign("end")
                justifyContent(JustifyContent.FlexEnd)
                backgroundImage("url(/saigonnight.jpg)")
            }
        }
    }

    val mainContent by style {
        boxSizing("border-box")
        display(DisplayStyle.Flex)
        flexDirection(FlexDirection.Column)
        minHeight(100.vh)
        alignItems(AlignItems.Stretch)
        justifyContent(JustifyContent.Stretch)
    }

    val navContainer by style {
        boxSizing("border-box")
        display(DisplayStyle.Flex)
        flexDirection(FlexDirection.Column)
        alignItems(AlignItems.Stretch)
//        padding(PaddingDefault)
        overflowX("hidden")
        property("box-shadow", "rgba(0, 0, 0, 0.125) 2px 2px 16px")
        backgroundColor(Color.white)
        borderRadius(CornerDefault)
        marginLeft(PaddingDefault)
        marginRight(PaddingDefault)
        property("max-width", "calc(100vw - ${PaddingDefault * 2})")
    }

    val navContent by style {
        display(DisplayStyle.Flex)
        flexDirection(FlexDirection.Column)
        alignItems(AlignItems.Stretch)
        boxSizing("border-box")
    }

    val cardContent by style {
        display(DisplayStyle.Flex)
        flexDirection(FlexDirection.Column)
        alignItems(AlignItems.FlexStart)
        padding(PaddingDefault)

        child(self, not(lastChild)) style {
            marginBottom(PaddingDefault)
        }

        media(mediaMinWidth(641.px)) {
            self style {
                padding(PaddingDefault * 1.5f)
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

    @OptIn(ExperimentalComposeWebApi::class)
    val card by style {
        cardStyle()
        position(Position.Relative)
        display(DisplayStyle.Flex)
        width(640.px)
        overflow("hidden")
        flexDirection(FlexDirection.ColumnReverse)
        cursor("pointer")
        property("aspect-ratio", ".75")
        property("will-change", "transform")
        property("transform-style", "preserve-3d")

        transitions {
            "transform" {
                duration = 500.ms
            }
        }

        transform {
            perspective(100.vw)
        }

//        self + hover style {
//            self style {
//                transform {
//                    perspective(100.vw)
//                    rotate3d(1f, 0, .5f, 6.deg)
//                    translate3d(0.cssRem, -1.cssRem, 0.cssRem)
//                }
//            }
//        }

        media(mediaMaxWidth(640.px)) {
            self style {
                width(100.percent)
                margin(PaddingDefault / 2)
            }
        }
    }

    val category by style {
        borderRadius(.5.cssRem)
        border(1.px, LineStyle.Solid, colors.primary)
        color(colors.primary)
        property("width", "fit-content")
        marginTop(.5.cssRem)
        padding(.5.cssRem, 1.cssRem)
    }

    val cardPost by style {
        backgroundColor(rgba(255, 255, 255, .96))
        padding(PaddingDefault)
        margin(PaddingDefault)
        color(Color.black)
        borderRadius(PaddingDefault)
        maxHeight(50.percent)
        boxSizing("border-box")
        overflowY("auto")
        fontSize(18.px)
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

    val mainContentCards by style {
        boxSizing("border-box")
        display(DisplayStyle.Flex)
        flexWrap(FlexWrap.Wrap)
        position(Position.Relative)

        child(self, className(card)) style {
            self style {
                width(320.px)
                marginTop(0.cssRem)
                marginLeft(0.cssRem)
            }
        }
    }

    val textarea by style {
        borderRadius(1.cssRem)
        border(1.px, LineStyle.Solid, colors.background)
        property("resize", "none")
        padding(1.cssRem)
        property("font-size", "inherit")
        fontFamily("inherit")
        boxSizing("border-box")

        backgroundColor(Color.white)
        media("(prefers-color-scheme: dark)") {
            self style {
                backgroundColor(colors.dark.background)
                color(Color.white)
                border(1.px, LineStyle.Solid, Color("#444444"))
            }
        }

        self + selector("::placeholder") style {
            media("(prefers-color-scheme: dark)") {
                self style {
                    color(Color.white)
                    opacity(.5)
                }
            }
        }
    }

    val profilePhotoText by style {
        borderRadius(100.percent)
        backgroundColor(colors.background)
        display(DisplayStyle.Flex)
        alignItems(AlignItems.Center)
        justifyContent(JustifyContent.Center)

        media("(prefers-color-scheme: dark)") {
            self style {
                backgroundColor(Color.black)
            }
        }
    }

    val profilePhotoPhoto by style {
        borderRadius(100.percent)
        backgroundColor(Styles.colors.background)
        backgroundPosition("center")
        backgroundSize("cover")
        flexShrink(0)

        media("(prefers-color-scheme: dark)") {
            self style {
                backgroundColor(Color.black)
            }
        }
    }

    val profilePhotoBorder by style {
        border(3.px, LineStyle.Solid, Color.white)

        media("(prefers-color-scheme: dark)") {
            self style {
                border(3.px, LineStyle.Solid, Color.black)
            }
        }
    }
}
