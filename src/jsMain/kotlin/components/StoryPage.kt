package components

import PaddingDefault
import Person
import Story
import androidx.compose.runtime.*
import app.softwork.routingcompose.Router
import baseUrl
import http
import io.ktor.client.call.*
import io.ktor.client.request.*
import json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import org.jetbrains.compose.web.attributes.ATarget
import org.jetbrains.compose.web.attributes.target
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text
import stories.StoryStyles
import kotlin.js.Date

@Composable
fun StoryPage(storyUrl: String, onStoryLoaded: (Story) -> Unit) {
    Style(StoryStyles)

    val scope = rememberCoroutineScope()
    val router = Router.current
    var isLoading by remember { mutableStateOf(true) }
    var story by remember { mutableStateOf<Story?>(null) }
    var storyContent by remember { mutableStateOf<List<StoryContent>?>(null) }

    LaunchedEffect(storyUrl) {
        isLoading = true
        try {
            story = http.get("$baseUrl/urls/stories/$storyUrl").body()
            onStoryLoaded(story!!)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    LaunchedEffect(story) {
        story?.let { story ->
            storyContent = json.parseToJsonElement(story.content ?: "[]").jsonArray.toList().mapNotNull { part ->
                part.jsonObject.toStoryContent()
            }.let { parts ->
                listOf(
                    StoryContent.Title(story.title ?: "", story.id!!),
                    StoryContent.Authors(story.publishDate, story.authors ?: emptyList()),
                ) + parts
            }
        }
    }

    if (!isLoading && story == null) {
        Div({
            classes(Styles.mainContent)
            style {
                display(DisplayStyle.Flex)
                minHeight(100.vh)
                width(100.percent)
                flexDirection(FlexDirection.Column)
                padding(PaddingDefault * 2)
                alignItems(AlignItems.Center)
                justifyContent(JustifyContent.FlexStart)
            }
        }) {
            Text("Story not found.")
        }
    } else {
        story?.let { story ->
            Div({
                classes(Styles.mainContent)
            }) {
                Div({
                    classes(Styles.navContainer)
                    style {
                        maxWidth(1200.px)
                        alignSelf(AlignSelf.Center)
                        marginBottom(1.cssRem)
                    }
                }) {
                    Div({
                        classes(Styles.navContent)
                    }) {
                        Div({
                            classes(Styles.cardContent)
                        }) {
                            if (storyContent != null) {
                                StoryContents(storyContent!!)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StoryContents(storyContent: List<StoryContent>) {
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
                    Text("${part.publishDate?.let { Date(it) }?.let { "Published on $it" } ?: "Draft"} by ")
                    part.authors.forEachIndexed { index, person ->
                        if (index > 0) {
                            Text(", ")
                        }
                        A(href = "/profile/${person.id}") {
                            Text(person.name ?: "Someone")
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
                    Text(part.text)
                }
            }
            is StoryContent.Cards -> {
                // todo
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
                // todo
            }
            else -> {}
        }
    }
}

@Serializable
sealed class StoryContent {
    object Divider : StoryContent()
    class Title(var title: String, val id: String) : StoryContent()
    class Authors(var publishDate: String?, var authors: List<Person>) : StoryContent()
    @Serializable class Section(var section: String) : StoryContent()
    @Serializable class Text(var text: String) : StoryContent()
    @Serializable class Cards(var cards: List<String>) : StoryContent()
    @Serializable class Photos(var photos: List<String>, var aspect: Float = 0.75f) : StoryContent()
    @Serializable class Audio(var audio: String) : StoryContent()
}

fun JsonObject.toStoryContent(): StoryContent? = get("content")?.jsonObject?.let { content ->
    when (get("type")?.jsonPrimitive?.content) {
        "section" -> json.decodeFromJsonElement<StoryContent.Section>(content)
        "text" -> json.decodeFromJsonElement<StoryContent.Text>(content)
        "cards" -> json.decodeFromJsonElement<StoryContent.Cards>(content)
        "photos" -> json.decodeFromJsonElement<StoryContent.Photos>(content)
        "audio" -> json.decodeFromJsonElement<StoryContent.Audio>(content)
        else -> null
    }
}
