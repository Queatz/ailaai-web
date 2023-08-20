package app

import Styles
import Styles.elevated
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*


object AppStyles : StyleSheet() {
    val baseLayout by style {
        width(100.vw)
        height(100.vh)
        overflow("hidden")
        display(DisplayStyle.Flex)
        backgroundColor(Color("#fafafa"))
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
        elevated()
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
    @OptIn(ExperimentalComposeWebApi::class)
    val groupItem by style {
        padding(.5.cssRem, 1.cssRem)
        borderRadius(1.cssRem)
        cursor("pointer")
        display(DisplayStyle.Flex)
        alignItems(AlignItems.Center)

        hover(self) style {
            backgroundColor(Styles.colors.background)
        }

        transitions {
            "color" {
                duration = 100.ms
            }
            "background-color" {
                duration = 100.ms
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
        borderRadius(1.cssRem)
        border(1.px, LineStyle.Solid, Styles.colors.background)
        whiteSpace("pre-wrap")

        self + className(myMessage) style {
            backgroundColor(Styles.colors.background)
            property("border", "none")
        }
    }
}
