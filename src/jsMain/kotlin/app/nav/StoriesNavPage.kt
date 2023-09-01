package app.nav

import PaddingDefault
import Story
import androidx.compose.runtime.*
import app.AppStyles
import app.messaages.preview
import application
import baseUrl
import components.GroupPhoto
import components.IconButton
import components.Loading
import http
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.utils.io.charsets.*
import kotlinx.browser.window
import kotlinx.coroutines.launch
import lib.formatDistanceToNow
import org.jetbrains.compose.web.attributes.autoFocus
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import stories.storyStatus
import stories.textContent
import kotlin.js.Date

@Composable
fun StoriesNavPage(selected: Story?, onSelected: (Story?) -> Unit) {
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
        myStories = try {
            http.get("$baseUrl/me/stories") {
                contentType(ContentType.Application.Json.withCharset(Charsets.UTF_8))
                bearerAuth(application.bearer)
            }.body<List<Story>>().sortedByDescending { it.publishDate == null }
        } catch (e: Throwable) {
            e.printStackTrace()
            emptyList()
        }

        isLoading = false
    }

    LaunchedEffect(Unit) {
        reload()
    }

    NavTopBar(me, "Stories") {
        IconButton("search", "Search", styles = {
            marginRight(1.cssRem)
        }) {
            showSearch = !showSearch
        }
        IconButton("add", "New story", styles = {
            marginRight(1.cssRem)
        }) {
            scope.launch {
                val title = window.prompt("Story title")
                if (title == null) return@launch
                try {
                    val story = http.post("$baseUrl/stories") {
                        setBody(Story(title = title))
                        contentType(ContentType.Application.Json.withCharset(Charsets.UTF_8))
                        bearerAuth(application.bearer)
                    }.body<Story>()
                    reload()
                } catch (e: Throwable) {
                    e.printStackTrace()
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
                NavMenuItem("explore", "Explore nearby", selected = selected == null) {
                    onSelected(null)
                }
                NavMenuItem("favorite", "Saved") {
                    // todo
                }
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
