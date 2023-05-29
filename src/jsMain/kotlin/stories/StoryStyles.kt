package stories

import CornerDefault
import PaddingDefault
import org.jetbrains.compose.web.css.*

object StoryStyles : StyleSheet() {
    object colors {
//        val primary = Color("#006689")
    }

    val contentTitle by style {
        boxSizing("border-box")
        display(DisplayStyle.Flex)
        alignItems(AlignItems.Stretch)
        justifyContent(JustifyContent.Stretch)
        fontSize(48.px)
    }

    val contentAuthors by style {
        boxSizing("border-box")
        display(DisplayStyle.Flex)
        alignItems(AlignItems.Stretch)
        justifyContent(JustifyContent.Stretch)
        opacity(0.5f)
        fontSize(14.px)
    }


    val contentSection by style {
        boxSizing("border-box")
        display(DisplayStyle.Flex)
        alignItems(AlignItems.Stretch)
        justifyContent(JustifyContent.Stretch)
        fontSize(18.px)
        fontWeight("bold")
    }

    val contentText by style {
        boxSizing("border-box")
        display(DisplayStyle.Flex)
        alignItems(AlignItems.Stretch)
        justifyContent(JustifyContent.Stretch)
        whiteSpace("pre-wrap")
        fontSize(16.px)
    }

    val contentPhotos by style {
        boxSizing("border-box")
        display(DisplayStyle.Flex)
        flexWrap(FlexWrap.Wrap)
        alignItems(AlignItems.Stretch)
        justifyContent(JustifyContent.Stretch)
    }

    val contentPhotosPhoto by style {
        backgroundPosition("center")
        backgroundSize("cover")
        borderRadius(CornerDefault)
        height(480.px)
        maxHeight(100.vh)
        marginRight(PaddingDefault)
        marginBottom(PaddingDefault)

        media(mediaMaxWidth(640.px)) {
            self style {
                width(100.percent)
                property("max-width", "calc(100vw - ${PaddingDefault * 2})")
            }
        }
    }
}