package app.nav

import Card
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
import inputDialog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import notBlank
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLElement
import r
import saves

sealed class CardNav {
    data object Explore : CardNav()
    data object Saved : CardNav()
    data class Selected(val card: Card, val subCard: Card? = null) : CardNav()
}

@Composable
fun CardsNavPage(cardUpdates: Flow<Card>, nav: CardNav, onSelected: (CardNav) -> Unit, onProfileClick: () -> Unit) {
    val cardId = (nav as? CardNav.Selected)?.card?.id
    val subCardId = (nav as? CardNav.Selected)?.subCard?.id

    val me by application.me.collectAsState()
    val saved by saves.cards.collectAsState()
    val scope = rememberCoroutineScope()


    var isLoading by remember { mutableStateOf(true) }

    var showSearch by remember(cardId) {
        mutableStateOf(false)
    }

    var searchText by remember(cardId) {
        mutableStateOf("")
    }

    var myCards by remember { mutableStateOf(emptyList<Card>()) }

    val childCards = remember(myCards, cardId) {
        myCards.filter { it.parent == cardId }
    }

    val shownCards = remember(myCards, searchText, saved) {
        val search = searchText.trim()
        (if (searchText.isBlank()) {
            myCards//.filter { it.parent == null }
        } else {
            myCards.filter {
                (it.name?.contains(search, true) ?: false)
            }
        }).sortedByDescending { saved.any { save -> it.id == save.id } }
    }

    suspend fun reload() {
        api.myCards {
            myCards = it
            if (cardId != null) {
                val subCard = myCards.firstOrNull { it.id == subCardId }
                onSelected(myCards.firstOrNull { it.id == cardId }?.let {
                    CardNav.Selected(it, subCard)
                } ?: CardNav.Explore)
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

    NavTopBar(me, "Pages", onProfileClick = onProfileClick) {
        IconButton("search", "Search", styles = {
        }) {
            showSearch = !showSearch
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
                NavMenuItem("distance", "Explore nearby", selected = nav == CardNav.Explore) {
                    onSelected(CardNav.Explore)
                }
                NavMenuItem("favorite", "Saved", selected = nav == CardNav.Saved) {
                    onSelected(CardNav.Saved)
                }
                Div({
                    style {
                        height(1.r)
                    }
                }) {

                }
            }
            val selected = (nav as? CardNav.Selected)?.let { it.subCard ?: it.card }
            shownCards.forEach {
                CardItem(it, (nav as? CardNav.Selected)?.subCard == null, selected == it, saved.any { save -> save.id == it.id }, it.active == true) { navigate ->
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

@Composable
fun CardItem(card: Card, scroll: Boolean, selected: Boolean, saved: Boolean, published: Boolean, onSelected: (Boolean) -> Unit) {
    var ref by remember {
        mutableStateOf<HTMLElement?>(null)
    }

    if (scroll) {
        LaunchedEffect(card, selected, ref) {
            if (selected) {
                ref?.let { ref ->
                    val rect = ref.getBoundingClientRect()
                    val parentRect = ref.offsetParent!!.getBoundingClientRect()
                    if (rect.y > parentRect.y + parentRect.height || rect.y + rect.height < parentRect.y) {
                        ref.scrollIntoView()
                    }
                }
            }
        }
    }

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
            onSelected(false)
        }

        onDoubleClick {
            onSelected(true)
        }

        if (scroll) {
            ref {
                ref = it
                onDispose { }
            }
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
                Text(card.name?.notBlank ?: "New page")
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
                marginLeft(.5.r)
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
            if (published) {
                Icon("toggle_on", title = "Page is published") {
                    fontSize(22.px)
                    color(Styles.colors.primary)
                    marginLeft(.5.r)
                }
            }
            if (saved) {
                Icon("favorite", title = "Page is saved") {
                    fontSize(18.px)
                    color(Styles.colors.secondary)
                    marginLeft(.5.r)
                }
            }
        }
    }
}
