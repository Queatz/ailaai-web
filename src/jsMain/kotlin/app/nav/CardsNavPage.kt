package app.nav

import androidx.compose.runtime.*
import api
import app.ailaai.api.myCards
import app.ailaai.api.newCard
import app.components.Spacer
import app.menu.Menu
import application
import com.queatz.db.Card
import components.IconButton
import components.Loading
import inputDialog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.DOMRect
import org.w3c.dom.HTMLElement
import r
import saves

sealed class CardNav {
    data object Friends : CardNav()
    data object Local : CardNav()
    data object Saved : CardNav()
    data class Selected(val card: Card, val subCard: Card? = null) : CardNav()
}

enum class CardFilter {
    Published,
    NotPublished,
    NoParent
}

@Composable
fun CardsNavPage(cardUpdates: Flow<Card>, nav: CardNav, onSelected: (CardNav) -> Unit, onProfileClick: () -> Unit) {
    val cardId = (nav as? CardNav.Selected)?.card?.id
    val subCardId = (nav as? CardNav.Selected)?.subCard?.id

    val me by application.me.collectAsState()
    val saved by saves.cards.collectAsState()
    val scope = rememberCoroutineScope()

    var filterMenuTarget by remember {
        mutableStateOf<DOMRect?>(null)
    }

    var isLoading by remember { mutableStateOf(true) }

    var showSearch by remember(cardId) {
        mutableStateOf(false)
    }

    var searchText by remember(cardId) {
        mutableStateOf("")
    }

    var filters by remember {
        mutableStateOf(emptySet<CardFilter>())
    }

    var myCards by remember { mutableStateOf(emptyList<Card>()) }

    val childCards = remember(myCards, cardId) {
        myCards.filter { it.parent == cardId }
    }

    val shownCards = remember(myCards, searchText, saved, filters) {
        val search = searchText.trim()
        (if (searchText.isBlank()) {
            myCards//.filter { it.parent == null }
        } else {
            myCards.filter {
                (it.name?.contains(search, true) ?: false)
            }
        }).sortedByDescending { saved.any { save -> it.id == save.id } }.let {
            if (filters.isNotEmpty()) {
                it.filter { card ->
                    filters.none {
                        when (it) {
                            CardFilter.Published -> card.active != true
                            CardFilter.NotPublished -> card.active == true
                            CardFilter.NoParent -> card.parent != null
                        }
                    }
                }
            } else {
                it
            }
        }
    }

    suspend fun reload() {
        api.myCards {
            myCards = it
            if (cardId != null) {
                val subCard = myCards.firstOrNull { it.id == subCardId }
                onSelected(myCards.firstOrNull { it.id == cardId }?.let {
                    CardNav.Selected(it, subCard)
                } ?: CardNav.Local)
            }
        }

        isLoading = false
    }

    // todo if (selected) is not passed in, then selected is always null in reload()
    // https://youtrack.jetbrains.com/issue/KT-61632/Kotlin-JS-argument-scope-bug
    LaunchedEffect(cardId, subCardId) {
        reload()
    }

    // todo if (selected) is not passed in, then selected is always null in reload()
    // https://youtrack.jetbrains.com/issue/KT-61632/Kotlin-JS-argument-scope-bug
    LaunchedEffect(cardId, subCardId) {
        cardUpdates.collectLatest {
            reload()
        }
    }

    if (filterMenuTarget != null) {
        Menu(
            {
                filterMenuTarget = null
            },
            filterMenuTarget!!
        ) {
            item("Published", icon = if (CardFilter.Published in filters) "check" else null) {
                if (CardFilter.Published in filters) {
                    filters -= CardFilter.Published
                } else {
                    filters -= CardFilter.NotPublished
                    filters += CardFilter.Published
                }
            }
            item("Not published", icon = if (CardFilter.NotPublished in filters) "check" else null) {
                if (CardFilter.NotPublished in filters) {
                    filters -= CardFilter.NotPublished
                } else {
                    filters -= CardFilter.Published
                    filters += CardFilter.NotPublished
                }
            }
            item("Root pages", icon = if (CardFilter.NoParent in filters) "check" else null) {
                if (CardFilter.NoParent in filters) {
                    filters -= CardFilter.NoParent
                } else {
                    filters += CardFilter.NoParent
                }
            }
        }
    }

    NavTopBar(me, "Pages", onProfileClick = onProfileClick) {
        IconButton("search", "Search", styles = {
        }) {
            showSearch = !showSearch
        }
        IconButton("filter_list", "Filter", count = filters.size, styles = {
        }) {
            filterMenuTarget =
                if (filterMenuTarget == null) (it.target as HTMLElement).getBoundingClientRect() else null
        }

        IconButton(
            "add",
            "New page",
            styles = {
                marginRight(.5.r)
            }
        ) {
            scope.launch {
                val result = inputDialog(
                    "New page",
                    "Title",
                    "Create"
                )

                if (result == null) return@launch

                api.newCard(Card(name = result)) {
                    onSelected(CardNav.Selected(it))
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
                padding(1.r / 2)
            }
        }) {
            if (!showSearch) {
                NavMenuItem("group", "Friends", selected = nav == CardNav.Friends) {
                    onSelected(CardNav.Friends)
                }
                NavMenuItem("location_on", "Local", selected = nav == CardNav.Local) {
                    onSelected(CardNav.Local)
                }
                NavMenuItem("favorite", "Saved", selected = nav == CardNav.Saved) {
                    onSelected(CardNav.Saved)
                }
                Spacer()
            }
            if (shownCards.isEmpty()) {
                Div({
                    style {
                        display(DisplayStyle.Flex)
                        alignItems(AlignItems.Center)
                        justifyContent(JustifyContent.Center)
                        opacity(.5)
                        padding(1.r)
                    }
                }) {
                    Text("No pages")
                }
            } else {
                val selected = (nav as? CardNav.Selected)?.let { it.subCard ?: it.card }
                shownCards.forEach {
                    CardItem(
                        it,
                        (nav as? CardNav.Selected)?.subCard == null,
                        selected == it,
                        saved.any { save -> save.id == it.id },
                        it.active == true
                    ) { _ ->
                        onSelected(CardNav.Selected(it))
                    }
                    if (it.id == cardId && childCards.isNotEmpty()) {
                        Div({
                            style { marginLeft(1.r) }
                        }) {
//                    val selectedSubCard = (nav as? CardNav.Selected)?.let { it.subCard }
                            childCards.forEach {
                                CardItem(
                                    it,
                                    true,
                                    selected == it,
                                    saved.any { save -> save.id == it.id },
                                    it.active == true
                                ) { navigate ->
                                    val card = (nav as CardNav.Selected).card

                                    if (navigate) {
                                        onSelected(CardNav.Selected(it))
                                    } else {
                                        onSelected(CardNav.Selected(card, it))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
