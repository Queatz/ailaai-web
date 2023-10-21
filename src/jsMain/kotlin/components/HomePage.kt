package components

import Styles
import androidx.compose.runtime.*
import api
import app.ailaai.api.cards
import appString
import appText
import com.queatz.db.Card
import com.queatz.db.Geo
import kotlinx.coroutines.delay
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import r

@Composable
fun HomePage() {
    Div({
        style {
            property("margin", "${1.r} auto")
            maxWidth(1200.px)
            padding(0.r, 1.r, 1.r, 1.r)
            fontSize(22.px)
            lineHeight("1.5")
            minHeight(100.vh)
        }
    }) {
        Div({
            classes(Styles.mainHeader)
        }) {
            Div {
                Text(appString { homeTagline })
            }
        }
        H3 {
            Text(appString { homeAboutTitle })
        }
        Div {
            Text(appString { homeAboutDescription })
            Br()
        }
        DownloadAppButton()
        var searchText by remember { mutableStateOf("") }
        var isLoading by remember { mutableStateOf(false) }
        var searchResults by remember { mutableStateOf(listOf<Card>()) }

        SearchField(searchText, appString { searchCity }, modifier = {
            marginTop(2.r)
        }) {
            searchText = it
        }

        LaunchedEffect(searchText) {
            isLoading = true
            delay(250)


            if (searchText.isNotBlank()) {
                // HCMC
                api.cards(Geo(10.7915858, 106.7426523), search = searchText, onError = {
                    searchResults = emptyList()
                }) {
                    searchResults = it
                }
            } else {
                searchResults = emptyList()
            }
            isLoading = false
        }

        when (searchText.isBlank()) {
            true -> {
                listOf(
                    appString { peopleToKnow } to listOf(
                        "11389583",
                        "11156377",
                        "10455696",
                        "12319827",
                        "9914441"
                    ).shuffled().take(3),
                    appString { placesToKnow } to listOf("9879608", "10102613")
                )
            }

            false -> {
                listOf(
                    (if (isLoading) appString { searching } else appString { this.searchResults }) to searchResults,
                )
            }
        }.forEach { (category, cards) ->
            H3 {
                Text(category)
            }
            Div({
                classes(Styles.mainContentCards)
            }) {
                if (cards.isEmpty()) {
                    if (!isLoading) {
                        Span({
                            style {
                                color(Styles.colors.secondary)
                            }
                        }) {
                            appText { noCards }
                        }
                    }
                } else {
                    cards.forEach { card ->
                        when (card) {
                            is String -> CardItem(card) {
                                margin(1.r)
                            }

                            is Card -> CardItem(card, styles = {
                                margin(1.r)
                            })
                        }
                    }
                }
            }
        }
    }
}
