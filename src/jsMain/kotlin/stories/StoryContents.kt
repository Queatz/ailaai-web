package stories

import Styles
import androidx.compose.runtime.Composable
import appString
import baseUrl
import components.CardItem
import components.Icon
import components.LinkifyText
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.dom.Style
import kotlin.js.Date

@Composable
fun StoryContents(storyContent: List<StoryContent>) {
    Style(StoryStyles)
    storyContent.forEach { part ->
        when (part) {
            is StoryContent.Title -> {
                Div({
                    classes(StoryStyles.contentTitle)
                }) {
                    Text(part.title)
                }
            }
            is StoryContent.Authors -> {
                Div({
                    classes(StoryStyles.contentAuthors)
                }) {
                    Span {
                        Text("${part.publishDate?.let { Date(it) } ?: appString { draft }} ${appString { inlineBy }} ")
                        part.authors.forEachIndexed { index, person ->
                            if (index > 0) {
                                Text(", ")
                            }
                            A(href = "/profile/${person.id}") {
                                Text(person.name ?: appString { someone })
                            }
                        }
                    }
                }
            }
            is StoryContent.Section -> {
                Div({
                    classes(StoryStyles.contentSection)
                }) {
                    Text(part.section)
                }
            }
            is StoryContent.Text -> {
                Div({
                    classes(StoryStyles.contentText)
                }) {
                    LinkifyText(part.text)
                }
            }
            is StoryContent.Cards -> {
                Div({
                    classes(StoryStyles.contentCards)
                }) {
                    part.cards.forEach { card ->
                        CardItem(card)
                    }
                }
            }
            is StoryContent.Photos -> {
                Div({
                    classes(StoryStyles.contentPhotos)
                }) {
                    part.photos.forEach { photo ->
                        Div({
                            classes(StoryStyles.contentPhotosPhoto)
                            style {
                                backgroundColor(Styles.colors.background)
                                backgroundImage("url($baseUrl$photo)")
                                property("aspect-ratio", "${part.aspect}")

                            }
                        })
                    }
                }
            }
            is StoryContent.Audio -> {
                Audio({
                    classes(StoryStyles.contentAudio)
                    attr("controls", "")
                    style {
                        width(100.percent)
                    }
                }) {
                    Source({
                        attr("src", "$baseUrl${part.audio}")
                        attr("type", "audio/mp4")
                    })
                }
            }
            is StoryContent.Divider -> {
                Div({
                    classes(StoryStyles.divider)
                }) {
                    Icon("flare")
                }
            }
        }
    }
}