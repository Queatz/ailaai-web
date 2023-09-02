package app.nav

import Card
import PaddingDefault
import Styles
import androidx.compose.runtime.*
import api
import app.AppStyles
import appString
import application
import components.Icon
import components.IconButton
import components.Loading
import focusable
import kotlinx.browser.window
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import notBlank
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import saves

sealed class CardNav {
    data object Explore : CardNav()
    data object Saved : CardNav()
    data class Selected(val card: Card) : CardNav()
}

@Composable
fun CardsNavPage(cardUpdates: Flow<Card>, nav: CardNav, onSelected: (CardNav) -> Unit) {
    val me by application.me.collectAsState()
    val saved by saves.cards.collectAsState()
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(true) }

    var showSearch by remember {
        mutableStateOf(false)
    }

    var searchText by remember {
        mutableStateOf("")
    }

    LaunchedEffect(nav) {
        searchText = ""
        showSearch = false
    }

    var myCards by remember { mutableStateOf(emptyList<Card>()) }

    val shownCards = remember(myCards, searchText, saved) {
        val search = searchText.trim()
        (if (searchText.isBlank()) {
            myCards
        } else {
            myCards.filter {
                (it.name?.contains(search, true) ?: false)
            }
        }).sortedByDescending { saved.any { save -> it.id == save.id } }
    }

    suspend fun reload() {
        val selectedId = (nav as? CardNav.Selected)?.card?.id

        api.myCards {
            myCards = it
        }

        isLoading = false

        if (selectedId != null) {
            onSelected(myCards.firstOrNull { it.id == selectedId }?.let {
                CardNav.Selected(it)
            } ?: CardNav.Explore)
        }
    }

    LaunchedEffect(Unit) {
        reload()
    }

    // todo if (selected) is not passed in, then selected is always null in reload()
    // https://youtrack.jetbrains.com/issue/KT-61632/Kotlin-JS-argument-scope-bug
    LaunchedEffect(nav) {
        cardUpdates.collectLatest {
            reload()
        }
    }

    NavTopBar(me, "Cards") {
        IconButton("search", "Search", styles = {
        }) {
            showSearch = !showSearch
        }
        IconButton("add", "New card", styles = {
            marginRight(.5.cssRem)
        }) {
            scope.launch {
                val name = window.prompt("Card title")

                if (name == null) return@launch

                api.newCard(Card(name = name)) {
                    onSelected(CardNav.Selected(it))
                    // todo this reloads old card
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
            if (!showSearch) {
                NavMenuItem("explore", "Explore nearby", selected = nav == CardNav.Explore) {
                    onSelected(CardNav.Explore)
                }
                NavMenuItem("favorite", "Saved", selected = nav == CardNav.Saved) {
                    onSelected(CardNav.Saved)
                }
                Div({
                    style {
                        height(1.cssRem)
                    }
                }) {

                }
            }
            val selected = (nav as? CardNav.Selected)?.card
            shownCards.forEach {
                CardItem(it, selected == it, saved.any { save -> save.id == it.id }, it.active == true) {
                    onSelected(CardNav.Selected(it))
                }
            }
        }
    }
}

@Composable
fun CardItem(card: Card, selected: Boolean, saved: Boolean, published: Boolean, onSelected: () -> Unit) {
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
        Div({
            style {
                marginLeft(.5.cssRem)
                flexShrink(0)
                display(DisplayStyle.Flex)
                alignItems(AlignItems.Center)
            }
        }) {
            if ((card.cardCount ?: 0) > 0) {
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
            if (saved) {
                Icon("favorite") {
                    fontSize(18.px)
                    color(Styles.colors.secondary)
                    marginLeft(.5.cssRem)
                }
            }
            if (published) {
                Icon("toggle_on") {
                    fontSize(22.px)
                    color(Styles.colors.primary)
                    marginLeft(.5.cssRem)
                }
            }
        }
    }
}
