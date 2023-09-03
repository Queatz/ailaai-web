package app.page

import Styles.elevated
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*

object SchedulePageStyles : StyleSheet() {
    val title by style {
        marginBottom(.5.cssRem)
        fontSize(18.px)
        display(DisplayStyle.Flex)
    }

    val section by style {
        borderRadius(1.cssRem)
        padding(.5.cssRem)
        marginBottom(1.cssRem)
        backgroundColor(Color.white)
        display(DisplayStyle.Flex)
        flexDirection(FlexDirection.Column)
        overflow("hidden")
        elevated()

        media("(prefers-color-scheme: dark)") {
            self style {
                backgroundColor(Styles.colors.dark.background)
                property("border", "none")
            }
        }
    }

    val rowActions by style {
        opacity(0)
        lineHeight("0")
        color(Styles.colors.primary)
    }

    val rowText by style {

    }

    val row by style {
        display(DisplayStyle.Flex)
        padding(.5.cssRem)
        borderRadius(.5.cssRem)
        cursor("pointer")
        alignItems(AlignItems.Center)
        opacity(1)

        self + hover style {
            backgroundColor(Styles.colors.background)

            child(self, className(rowActions)) style {
                opacity(1)
            }
        }

        self + focus style {
            backgroundColor(Styles.colors.background)

            child(self, className(rowActions)) style {
                opacity(1)
            }
        }

        media("(prefers-color-scheme: dark)") {
            self + hover style {
                backgroundColor(Color.black)
            }

            self + focus style {
                backgroundColor(Color.black)
            }

            child(self, className(rowActions)) style {
                color(Color.white)
            }
        }
    }
}
