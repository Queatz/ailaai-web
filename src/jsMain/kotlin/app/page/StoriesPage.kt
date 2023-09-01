package app.page

import Story
import Styles
import androidx.compose.runtime.*
import api
import app.FullPageLayout
import application
import components.Loading
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import stories.StoryContent
import stories.StoryContents
import stories.full

@Composable
fun StoriesPage(story: Story?) {
    val me by application.me.collectAsState()
    var storyContent by remember { mutableStateOf<List<StoryContent>>(emptyList()) }
    var isLoading by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(story) {
        isLoading = true

        if (story == null) {
            api.stories(me?.geo ?: listOf(10.7915858, 106.7426523)) { stories ->
                storyContent = stories.flatMapIndexed { index, it ->
                    if (index < stories.lastIndex) it.full() + StoryContent.Divider else it.full()
                }
            }
        } else {
            storyContent = story.full()
        }

        isLoading = false
    }

    if (isLoading) {
        Loading()
    } else {
        FullPageLayout {
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
