package app.nav

import PaddingDefault
import Story
import androidx.compose.runtime.*
import api
import app.AppStyles
import application
import components.IconButton
import components.Loading
import focusable
import kotlinx.browser.window
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text
import stories.storyStatus
import stories.textContent

@Composable
fun StoriesNavPage(storyUpdates: Flow<Story>, selected: Story?, onSelected: (Story?) -> Unit) {
    val me by application.me.collectAsState()
    val scope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(true) }
    var myStories by remember { mutableStateOf(emptyList<Story>()) }

    var showSearch by remember {
        mutableStateOf(false)
    }

    var searchText by remember {
        mutableStateOf("")
    }

    LaunchedEffect(selected) {
        searchText = ""
        showSearch = false
    }

    val shownStories = remember(myStories, searchText) {
        val search = searchText.trim()
        if (searchText.isBlank()) {
            myStories
        } else {
            myStories.filter {
                (it.title?.contains(search, true) ?: false)
            }
        }
    }

    suspend fun reload() {
        api.myStories {
            myStories = it
        }

        if (selected != null) {
            onSelected(myStories.firstOrNull { it.id == selected.id })
        }

        isLoading = false
    }

    LaunchedEffect(Unit) {
        reload()
    }

    // todo see youtrack, should not need (selected)
    LaunchedEffect(selected) {
        storyUpdates.collectLatest {
            reload()
        }
    }

    NavTopBar(me, "Stories") {
        IconButton("search", "Search", styles = {
        }) {
            showSearch = !showSearch
        }
        IconButton("add", "New story", styles = {
            marginRight(.5.cssRem)
        }) {
            scope.launch {
                val title = window.prompt("Story title")
                if (title == null) return@launch
                api.newStory(Story(title = title)) {
                    reload()
                }
            }
        }
    }
    if (showSearch) {
        NavSearchInput(searchText, { searchText = it }, onDismissRequest = {
            searchText = ""
            showSearch = false
        })
    }
    // todo this is same as groupsnavpage Should be NavMainContent
    if (isLoading) {
        Loading()
    } else {
        Div({
            style {
                overflowY("auto")
                overflowX("hidden")
                padding(PaddingDefault / 2)
            }
        }) {
            if (!showSearch) {
                NavMenuItem("distance", "Explore nearby", selected = selected == null) {
                    onSelected(null)
                }
//                NavMenuItem("favorite", "Saved") {
//                    // todo
//                }
                Div({
                    style {
                        height(1.cssRem)
                    }
                }) {

                }
            }
            shownStories.forEach { StoryItem(it, it == selected) { onSelected(it)} }
        }
    }
}

@Composable
fun StoryItem(story: Story, selected: Boolean, onSelected: () -> Unit) {
    Div({
        classes(
            listOf(AppStyles.groupItem) + if (selected) {
                listOf(AppStyles.groupItemSelected)
            } else {
                emptyList()
            }
        )
        focusable()
        onClick {
            onSelected()
        }
    }) {
        Div({
            style {
                width(0.px)
                flexGrow(1)
            }
        }) {
            Div({
                classes(AppStyles.groupItemName)
            }) {
                Text(story.title ?: "New story")
            }
            Div({
                classes(AppStyles.groupItemMessage)
            }) {
                Text(storyStatus(story.publishDate))
            }
            Div({
                classes(AppStyles.groupItemMessage)
            }) {
                Text(story.textContent())
            }
        }
    }
}
