package components

import Card
import PaddingDefault
import Strings.homeTagline
import Styles
import androidx.compose.runtime.*
import appString
import appText
import baseUrl
import http
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.delay
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.autoFocus
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*

@Composable
fun HomePage() {
    Div({
        style {
            property("margin", "$PaddingDefault auto")
            maxWidth(1200.px)
            padding(0.cssRem, 1.cssRem, 1.cssRem, 1.cssRem)
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
            Br()
        }
        Div {
            Text(appString { toJoinThePlatform })
            A("mailto:jacobaferrero@gmail.com?subject=${appString { inviteEmailSubject }}") {
                Text(appString { inlineSendMeAnEmail })
            }
            Text(" ${appString { engageToday }}")
        }
        DownloadAppButton()
        var searchText by remember { mutableStateOf("") }
        var isLoading by remember { mutableStateOf(false) }
        var searchResults by remember { mutableStateOf(listOf<Card>()) }
        Div({
            style {
                position(Position.Relative)
            }
        }) {
            val whomDoYouSeek = appString { whomDoYouSeek }
            Input(InputType.Text) {
                classes(Styles.textarea)
                style {
                    width(100.percent)
                    marginTop(2.cssRem)
                    paddingLeft(3.cssRem)
                }

                placeholder(whomDoYouSeek)

                onInput {
                    searchText = it.value
                }

                autoFocus()
            }
            Span({
                classes("material-symbols-outlined")
                style {
                    position(Position.Absolute)
                    property("z-index", "1")
                    property("pointer-events", "none")
                    color(Styles.colors.primary)
                    left(1.cssRem)
                    top(2.cssRem)
                    bottom(0.cssRem)
                    display(DisplayStyle.Flex)
                    alignItems(AlignItems.Center)
                }
            }) {
                Text("search")
            }
        }

        LaunchedEffect(searchText) {
            isLoading = true
            delay(250)
            searchResults = if (searchText.isNotBlank()) {
                try {
                    http.get("$baseUrl/cards") {
                        parameter("geo", "10.7915858,106.7426523") // HCMC
                        parameter("search", searchText)
                    }.body()
                } catch (e: Exception) {
                    e.printStackTrace()
                    emptyList()
                }
            } else {
                emptyList()
            }
            isLoading = false
        }

        when(searchText.isBlank()) {
            true -> {
                listOf(
                    appString { peopleToKnow } to listOf("11389583", "11156377", "10455696", "12319827", "9914441").shuffled().take(3),
                    appString { placesToKnow } to listOf("9879608", "10102613"),
                    appString { thingsToKnow } to listOf("2181697"),
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
                            appText { noCardsFound }
                        }
                    }
                } else {
                    cards.forEach { card ->
                        when (card) {
                            is String -> CardItem(card)
                            is Card -> CardItem(card)
                        }
                    }
                }
            }
        }
    }
}
