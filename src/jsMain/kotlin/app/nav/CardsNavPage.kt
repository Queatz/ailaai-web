package app.nav

import Card
import PaddingDefault
import androidx.compose.runtime.*
import app.AppStyles
import appString
import appText
import application
import baseUrl
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
import notBlank
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import stories.storyStatus
import stories.textContent
import kotlin.js.Date

@Composable
fun CardsNavPage(selected: Card?, onSelected: (Card?) -> Unit) {
    val me by application.me.collectAsState()
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(true) }

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

    var myCards by remember { mutableStateOf(emptyList<Card>()) }

    val shownCards = remember(myCards, searchText) {
        val search = searchText.trim()
        if (searchText.isBlank()) {
            myCards
        } else {
            myCards.filter {
                (it.name?.contains(search, true) ?: false)
            }
        }
    }

    suspend fun reload() {
        myCards = try {
            http.get("$baseUrl/me/cards") {
                contentType(ContentType.Application.Json.withCharset(Charsets.UTF_8))
                bearerAuth(application.bearer)
            }.body<List<Card>>()
        } catch (e: Throwable) {
            e.printStackTrace()
            emptyList()
        }

        isLoading = false
    }

    LaunchedEffect(Unit) {
        reload()
    }

    NavTopBar(me, "Cards") {
        IconButton("search", "Search", styles = {
            marginRight(1.cssRem)
        }) {
            showSearch = !showSearch
        }
        IconButton("add", "New card", styles = {
            marginRight(1.cssRem)
        }) {
            scope.launch {
                val name = window.prompt("Card title")
                if (name == null) return@launch
                try {
                    val card = http.post("$baseUrl/cards") {
                        setBody(Card(name = name))
                        contentType(ContentType.Application.Json.withCharset(Charsets.UTF_8))
                        bearerAuth(application.bearer)
                    }.body<Card>()
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
    if (isLoading) {
        Loading()
    } else {
        // todo this is same as groupsnavpage Should be NavMainContent
        Div({
            style {
                overflowY("auto")
                overflowX("hidden")
                padding(PaddingDefault / 2)
            }
        }) {
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
            shownCards.forEach {
                CardItem(it, selected == it) {
                    onSelected(it)
                }
            }
        }
    }
}

@Composable
fun CardItem(card: Card, selected: Boolean, onSelected: () -> Unit) {
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
                Text(card.name?.notBlank ?: "New card")
            }
            if (!card.location.isNullOrBlank()) {
                Div({
                    classes(AppStyles.groupItemMessage)
                }) {
                    Text(card.location ?: "")
                }
            }
        }
        if ((card.cardCount ?: 0) > 0) {
            Div({
                style {
                    marginLeft(.5.cssRem)
                    flexShrink(0)
                }
            }) {
                Span({
                    style {
                        color(Styles.colors.secondary)
                        fontSize(14.px)
                        opacity(.5)
                    }
                }) {
                    Text(
                        "${card.cardCount} ${appString { if (card.cardCount == 1) inlineCard else inlineCards }}"
                    )
                }
            }
        }
    }
}
