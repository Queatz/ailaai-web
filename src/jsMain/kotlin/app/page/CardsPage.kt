package app.page

import Card
import androidx.compose.runtime.*
import api
import app.FullPageLayout
import app.nav.CardNav
import application
import components.*
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
            is CardNav.Saved -> {
                api.saved {
                    cards = it.mapNotNull { it.card }
                }
            }

            is CardNav.Explore -> {
                api.explore(me?.geo ?: listOf(10.7915858, 106.7426523)) {
                    cards = it
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
                            is CardNav.Explore -> Text("No pages nearby")
                            is CardNav.Saved -> Text("No saved pages")
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
                MyCardPage(
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
                            onCard(CardNav.Explore)
                            onCardUpdated(it)
                        }
                    }
                )
            }
        }
    }
}

