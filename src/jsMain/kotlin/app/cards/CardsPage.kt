package app.cards

import androidx.compose.runtime.*
import api
import app.FullPageLayout
import app.ailaai.api.card
import app.ailaai.api.cards
import app.ailaai.api.savedCards
import app.nav.CardNav
import appText
import application
import com.queatz.db.*
import components.*
import defaultGeo
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*

@Composable
fun CardsPage(nav: CardNav, onCard: (CardNav) -> Unit, onCardUpdated: (Card) -> Unit) {
    Style(CardsPageStyles)

    val me by application.me.collectAsState()
    val scope = rememberCoroutineScope()
    var cards by remember(nav) {
        mutableStateOf(listOf<Card>())
    }
    var isLoading by remember(nav !is CardNav.Selected) {
        mutableStateOf(nav !is CardNav.Selected)
    }

    LaunchedEffect(nav) {
        if (nav !is CardNav.Selected) {
            isLoading = true
        }
    }

    suspend fun reload() {
        if (me == null) return

        when (nav) {
            is CardNav.Friends -> {
                api.cards(me?.geo?.asGeo() ?: defaultGeo) {
                    cards = it
                }
            }

            is CardNav.Local -> {
                api.cards(me?.geo?.asGeo() ?: defaultGeo, public = true) {
                    cards = it
                }
            }

            is CardNav.Saved -> {
                api.savedCards {
                    cards = it.mapNotNull { it.card }
                }
            }

            is CardNav.Selected -> {
                // Nothing to load
            }
        }
        isLoading = false
    }

    LaunchedEffect(nav) {
        reload()
    }

    if (isLoading) {
        Loading()
    } else {
        FullPageLayout(maxWidth = null) {
            if (nav !is CardNav.Selected) {
                if (cards.isEmpty()) {
                    Div({
                        style {
                            height(100.percent)
                            display(DisplayStyle.Flex)
                            alignItems(AlignItems.Center)
                            justifyContent(JustifyContent.Center)
                            opacity(.5)
                        }
                    }) {
                        when (nav) {
                            is CardNav.Local -> appText { noCardsNearby }
                            is CardNav.Saved -> appText { noSavedCards }
                            else -> {}
                        }
                    }
                } else {
                    Div(
                        {
                            classes(CardsPageStyles.layout)

                            style {
                                overflowX("hidden")
                                overflowY("auto")
                            }
                        }
                    ) {
                        cards.forEach {
                            CardItem(it, openInNewWindow = true)
                        }
                    }
                }
            } else {
                ExplorePage(
                    nav.subCard ?: nav.card,
                    {
                        onCard(CardNav.Selected(it))
                    },
                    onCardUpdated = {
                        onCardUpdated(it)
                    },
                    onCardDeleted = {
                        if (it.parent != null) {
                            scope.launch {
                                api.card(it.parent!!) {
                                    onCard(CardNav.Selected(it))
                                    onCardUpdated(it)
                                }
                            }
                        } else {
                            onCard(CardNav.Local)
                            onCardUpdated(it)
                        }
                    }
                )
            }
        }
    }
}

