package app.page

import Story
import Styles
import androidx.compose.runtime.*
import api
import app.FullPageLayout
import app.PageTopBar
import app.menu.Menu
import app.nav.CardNav
import application
import components.Loading
import kotlinx.browser.window
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.DOMRect
import org.w3c.dom.HTMLElement
import stories.StoryContent
import stories.StoryContents
import stories.full

@Composable
fun StoriesPage(story: Story?, onStoryUpdated: (Story) -> Unit) {
    val me by application.me.collectAsState()
    val scope = rememberCoroutineScope()
    var storyContent by remember { mutableStateOf<List<StoryContent>>(emptyList()) }
    var isLoading by remember {
        mutableStateOf(true)
    }
    var menuTarget by remember {
        mutableStateOf<DOMRect?>(null)
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

    menuTarget?.let { target ->
        Menu({ menuTarget = null }, target) {
            item("Rename") {
                scope.launch {
                    val title = window.prompt("Story title", story!!.title ?: "")

                    if (title == null) return@launch

                    api.updateStory(
                        story.id!!,
                        Story(title = title)
                    ) {
                        onStoryUpdated(it)
                    }
                }
            }
        }
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
        story?.let { story ->
            PageTopBar(
                ""
//                story.title?.notBlank ?: "New story"
            ) {
                menuTarget = if (menuTarget == null) (it.target as HTMLElement).getBoundingClientRect() else null
            }
        }
    }
}
