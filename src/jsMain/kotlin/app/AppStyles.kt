package app

import CornerDefault
import PaddingDefault
import Styles
import Styles.card
import Styles.elevated
import Styles.style
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*


object AppStyles : StyleSheet() {

    val baseLayout by style {
        width(100.vw)
        height(100.vh)
        overflow("hidden")
        display(DisplayStyle.Flex)

        media("(prefers-color-scheme: dark)") {
            self style {
                backgroundColor(Color.black)
                color(Color.white)
            }
        }
    }
    val sideLayout by style {
        width(24.cssRem)
        overflow("hidden")
        flexShrink(0)
        display(DisplayStyle.Flex)
        flexDirection(FlexDirection.ColumnReverse)
        marginTop(1.cssRem)
        marginLeft(1.cssRem)
        marginBottom(1.cssRem)
        property("z-index", "1")
        elevated()

        media("(prefers-color-scheme: dark)") {
            self style {
                backgroundColor(Styles.colors.dark.background)
            }
        }
    }
    val mainLayout by style {
        overflow("hidden")
        flexGrow(1)
        display(DisplayStyle.Flex)
        flexDirection(FlexDirection.ColumnReverse)
    }
    @OptIn(ExperimentalComposeWebApi::class)
    val bottomBar by style {
        display(DisplayStyle.Flex)

        child(self, selector("span")) style {
            flex(1)
            textAlign("center")
            display(DisplayStyle.Flex)
            alignItems(AlignItems.Center)
            justifyContent(JustifyContent.Center)
            padding(.5.cssRem)
            margin(.5.cssRem)
            borderRadius(4.cssRem)

            transitions {
                "color" {
                    duration = 100.ms
                }
                "background-color" {
                    duration = 100.ms
                }
            }
        }
    }
    val messages by style {
        flex(1)
        display(DisplayStyle.Flex)
        flexDirection(FlexDirection.ColumnReverse)
        overflowY("auto")
        overflowX("hidden")
    }
    val messageBar by style {
        flexShrink(0)
        display(DisplayStyle.Flex)
        margin(1.cssRem)
        flexDirection(FlexDirection.RowReverse)
        alignItems(AlignItems.Center)
    }

    val groupItem by style {
        padding(.5.cssRem, 1.cssRem)
        borderRadius(1.cssRem)
        cursor("pointer")
        display(DisplayStyle.Flex)
        alignItems(AlignItems.Center)

        hover(self) style {
            backgroundColor(Styles.colors.background)
        }

        media("(prefers-color-scheme: dark)") {
            self style {
                hover(self) style {
                    backgroundColor(Color.black)
                }
            }
        }

        @OptIn(ExperimentalComposeWebApi::class)
        transitions {
            "color" {
                duration = 100.ms
            }
            "background-color" {
                duration = 100.ms
            }
        }
    }

    val groupItemSelected by style {
        backgroundColor(Styles.colors.background)

        media("(prefers-color-scheme: dark)") {
            self style {
                backgroundColor(Color.black)
            }
        }
    }

    val groupItemName by style {

    }
    val groupItemMessage by style {
        color(Styles.colors.secondary)
        whiteSpace("nowrap")
        property("text-overflow", "ellipsis")
        overflow("hidden")
    }

    val myMessageLayout by style {

    }

    val myMessage by style {

    }

    val messageLayout by style {
        display(DisplayStyle.Flex)
        margin(.5.cssRem, 6.cssRem, 0.cssRem, 1.cssRem)

        self + className(myMessageLayout) style {
            margin(1.cssRem, 1.cssRem, 0.cssRem, 6.cssRem)
            justifyContent(JustifyContent.FlexEnd)
        }
    }

    val messageItem by style {
        padding(1.cssRem)
        backgroundColor(Color.white)
        borderRadius(1.cssRem)
        border(1.px, LineStyle.Solid, Styles.colors.background)
        whiteSpace("pre-wrap")

        self + className(myMessage) style {
            backgroundColor(Styles.colors.background)
            property("border", "none")
        }

        media("(prefers-color-scheme: dark)") {
            self style {
                self + className(myMessage) style {
                    backgroundColor(Styles.colors.dark.background)
                }
            }
        }

        media("(prefers-color-scheme: dark)") {
            self style {
                backgroundColor(Color.black)
                border(1.px, LineStyle.Solid, Color("#444444"))
            }
        }
    }

    val myMessageReply by style {

    }

    val messageReply by style {
        display(DisplayStyle.Flex)
        padding(PaddingDefault)
        position(Position.Relative)
        marginBottom(PaddingDefault / 2)
        borderRadius(
            CornerDefault / 2,
            CornerDefault,
            CornerDefault,
            CornerDefault / 2,
        )
        backgroundColor(Color("#fafafa"))
        property("border-left", "4px solid ${Styles.colors.background}")

        self + className(myMessageReply) style {
            flexDirection(FlexDirection.RowReverse)
            borderRadius(
                CornerDefault,
                CornerDefault / 2,
                CornerDefault / 2,
                CornerDefault,
            )
            property("border-left", "none")
            property("border-right", "4px solid ${Styles.colors.background}")
        }

        media("(prefers-color-scheme: dark)") {
            self style {
                backgroundColor(Styles.colors.dark.background)
            }
        }
    }

    val iconButton by style {

    }

    val tray by style {
        display(DisplayStyle.Flex)
        height(18.cssRem)
        maxHeight(50.vh)
        overflowX("hidden")
        overflowY("auto")
        flexDirection(FlexDirection.Column)
        backgroundColor(Color("#fafafa"))
        border(1.px, LineStyle.Solid, Color("#e4e4e4"))
        borderRadius(CornerDefault)

        media("(prefers-color-scheme: dark)") {
            self style {
                backgroundColor(Styles.colors.dark.background)
                border(1.px, LineStyle.Solid, Color("#444444"))
            }
        }
    }

    val messageItemPhoto by style {
        backgroundColor(Styles.colors.background)
        height(320.px)
        maxHeight(100.vw)
        maxWidth(100.percent)
        borderRadius(CornerDefault)
        border(3.px, LineStyle.Solid, Color.white)
        cursor("pointer")
        property("object-fit", "cover")

        media("(prefers-color-scheme: dark)") {
            self style {
                backgroundColor(Color.black)
                border(3.px, LineStyle.Solid, Color.black)
            }
        }
    }

    val stickerMessage by style {
        borderRadius(4.cssRem)
        backgroundColor(Color.white)
        padding(PaddingDefault / 2, PaddingDefault)
        whiteSpace("nowrap")
        property("z-index", "1")

        media("(prefers-color-scheme: dark)") {
            self style {
                backgroundColor(Styles.colors.dark.background)
                color(Color.white)
            }
        }
    }

    val messageItemStory by style {
        borderRadius(CornerDefault)
        backgroundColor(Color.white)
        property("box-shadow", "rgba(0, 0, 0, 0.125) 1px 1px 4px")
        padding(PaddingDefault)
        cursor("pointer")
        overflow("hidden")
        maxWidth(36.cssRem)

        media("(prefers-color-scheme: dark)") {
            self style {
                backgroundColor(Styles.colors.dark.background)
            }
        }
    }
}
