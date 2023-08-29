package components

import PaddingDefault
import Story
import Styles
import androidx.compose.runtime.*
import app.softwork.routingcompose.Router
import appString
import baseUrl
import http
import io.ktor.client.call.*
import io.ktor.client.request.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import stories.*

@Composable
fun StoryPage(storyUrl: String, onStoryLoaded: (Story) -> Unit) {
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
        } catch (e: Throwable) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    LaunchedEffect(story) {
        story?.let { story ->
            storyContent = story.full()
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
            Text(appString { storyNotFound })
        }
    } else {
        story?.let { story ->
            Div({
                classes(Styles.mainContent)
            }) {
                Div({
                    classes(Styles.navContainer)
                    style {
                        maxWidth(960.px)
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
