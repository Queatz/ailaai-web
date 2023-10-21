package app.page

import Styles
import androidx.compose.runtime.*
import api
import app.FullPageLayout
import app.PageTopBar
import app.menu.Menu
import app.nav.StoryNav
import appText
import application
import com.queatz.db.*
import components.Loading
import defaultGeo
import dialog
import inputDialog
import kotlinx.browser.window
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Img
import org.w3c.dom.DOMRect
import org.w3c.dom.HTMLElement
import qr
import r
import stories.StoryContent
import stories.StoryContents
import stories.full
import webBaseUrl

@Composable
fun StoriesPage(selected: StoryNav, onStoryUpdated: (Story) -> Unit) {
    val me by application.me.collectAsState()
    val scope = rememberCoroutineScope()
    var storyContent by remember { mutableStateOf<List<StoryContent>>(emptyList()) }
    var isLoading by remember {
        mutableStateOf(true)
    }
    var menuTarget by remember {
        mutableStateOf<DOMRect?>(null)
    }

    LaunchedEffect(selected) {
        isLoading = true

        when (selected) {
            is StoryNav.Friends -> {
                api.stories(me?.geo ?: defaultGeo) { stories ->
                    storyContent = stories.flatMapIndexed { index, it ->
                        if (index < stories.lastIndex) it.full() + StoryContent.Divider else it.full()
                    }
                }
            }

            is StoryNav.Local -> {
                api.stories(me?.geo ?: defaultGeo, public = true) { stories ->
                    storyContent = stories.flatMapIndexed { index, it ->
                        if (index < stories.lastIndex) it.full() + StoryContent.Divider else it.full()
                    }
                }
            }
            is StoryNav.Saved -> {}
            is StoryNav.Selected -> {
                storyContent = selected.story.full()
            }
        }

        isLoading = false
    }

    (selected as? StoryNav.Selected)?.story?.let { story ->
        menuTarget?.let { target ->
            Menu({ menuTarget = null }, target) {
                item("Open in new tab", icon = "open_in_new") {
                    window.open("/story/${story!!.id}", target = "_blank")
                }
                item("Rename") {
                    scope.launch {
                        val title = inputDialog(
                            "Story title",
                            "",
                            "Update",
                            defaultValue = story!!.title ?: ""
                        )

                        if (title == null) return@launch

                        api.updateStory(
                            story.id!!,
                            Story(title = title)
                        ) {
                            onStoryUpdated(it)
                        }
                    }
                }

                item("QR code") {
                    scope.launch {
                        dialog("", cancelButton = null) {
                            val qrCode = remember {
                                "$webBaseUrl/story/${story.id!!}".qr
                            }
                            Img(src = qrCode) {
                                style {
                                    borderRadius(1.r)
                                }
                            }
                        }
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
                    padding(1.r)
                }
            }) {
                if (storyContent.isEmpty()) {
                    Div({
                        style {
                            color(Styles.colors.secondary)
                            padding(1.r)
                            width(100.percent)
                            display(DisplayStyle.Flex)
                            flexDirection(FlexDirection.Column)
                            alignItems(AlignItems.Center)
                        }
                    }) {
                        appText { noStories }
                    }
                } else {
                    StoryContents(storyContent, openInNewWindow = true)
                }
            }
        }
        if (selected is StoryNav.Selected) {
            PageTopBar(
                ""
//                story.title?.notBlank ?: "New story"
            ) {
                menuTarget = if (menuTarget == null) (it.target as HTMLElement).getBoundingClientRect() else null
            }
        }
    }
}
