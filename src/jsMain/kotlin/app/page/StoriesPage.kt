package app.page

import Story
import androidx.compose.runtime.*
import application
import baseUrl
import components.Loading
import http
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.utils.io.charsets.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import stories.StoryContent
import stories.StoryContents
import stories.full

@Composable
fun StoriesPage() {
    val me by application.me.collectAsState()
    var storyContent by remember { mutableStateOf<List<StoryContent>>(emptyList()) }
    var isLoading by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(Unit) {
        try {
            val stories = http.get("$baseUrl/stories") {
                parameter("geo", me?.geo?.joinToString(",") ?: "0,0") // todo
                contentType(ContentType.Application.Json.withCharset(Charsets.UTF_8))
                bearerAuth(application.bearer)
            }.body<List<Story>>()
            storyContent = stories.flatMapIndexed { index, it ->
                if (index < stories.lastIndex) it.full() + StoryContent.Divider else it.full()
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        isLoading = false
    }

    if (isLoading) {
        Loading()
    } else {
        Div({
            style {
                display(DisplayStyle.Flex)
                flexDirection(FlexDirection.Column)
                width(100.percent)
                height(100.percent)
                overflowX("hidden")
                overflowY("auto")
            }
        }) {
            Div({
                style {
                    display(DisplayStyle.Flex)
                    flexDirection(FlexDirection.Column)
                    width(100.percent)
                    height(100.percent)
                    alignItems(AlignItems.Stretch)
                    maxWidth(960.px)
                    alignSelf(AlignSelf.Center)
                }
            }) {
                Div({
                    classes(Styles.cardContent)
                    style {
                        display(DisplayStyle.Flex)
                        flexDirection(FlexDirection.Column)
                        padding(1.cssRem)
                    }
                }) {
                    StoryContents(storyContent)
                }
            }
        }
    }
}
