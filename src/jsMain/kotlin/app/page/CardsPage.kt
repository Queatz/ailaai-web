package app.page

import Card
import androidx.compose.runtime.*
import app.FullPageLayout
import app.PageTopBar
import app.nav.NavSearchInput
import application
import baseUrl
import components.CardItem
import components.CardPhotoOrVideo
import components.Loading
import components.getConversation
import http
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.utils.io.charsets.*
import kotlinx.coroutines.launch
import notBlank
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text
import stories.StoryContent

@Composable
fun CardsPage(card: Card?, onCard: (Card?) -> Unit) {
    Style(CardsPageStyles)
    val me by application.me.collectAsState()
    var cards by remember {
        mutableStateOf(listOf<Card>())
    }
    var isLoading by remember {
        mutableStateOf(true)
    }

    suspend fun reload() {
        if (me == null) return
        try {
            cards = http.get("$baseUrl/cards") {
                parameter("geo", me?.geo?.joinToString(",") ?: "10.7915858,106.7426523") // todo
                contentType(ContentType.Application.Json.withCharset(Charsets.UTF_8))
                bearerAuth(application.bearer)
            }.body()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        isLoading = false
    }

    LaunchedEffect(me) {
        reload()
    }

    if (isLoading) {
        Loading()
    } else {
        FullPageLayout(maxWidth = null) {
            if (card == null) {
                Div(
                    {
                        classes(CardsPageStyles.layout)
                    }
                ) {
                    cards.forEach {
                        CardItem(it)
                    }
                }
            } else {
                MyCardPage(card) {
                    onCard(it)
                }
            }
        }
    }
}

@Composable
fun MyCardPage(card: Card, onCard: (Card) -> Unit) {
    val me by application.me.collectAsState()
    val scope = rememberCoroutineScope()

    var newCardTitle by remember(card) {
        mutableStateOf("")
    }

    var cards by remember(card) {
        mutableStateOf(listOf<Card>())
    }

    var isLoading by remember(card) {
        mutableStateOf(true)
    }

    suspend fun reload() {
        if (me == null) return
        try {
            cards = http.get("$baseUrl/cards/${card.id!!}/cards") {
                contentType(ContentType.Application.Json.withCharset(Charsets.UTF_8))
                bearerAuth(application.bearer)
            }.body()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        isLoading = false
    }

    LaunchedEffect(card) {
        reload()
    }

    fun newSubCard(inCard: Card, name: String) {
        scope.launch {
            try {
                val card = http.post("$baseUrl/cards") {
                    setBody(Card(name = name, parent = inCard.id!!))
                    contentType(ContentType.Application.Json.withCharset(Charsets.UTF_8))
                    bearerAuth(application.bearer)
                }.body<Card>()
                reload()
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    PageTopBar(
        card.name?.notBlank ?: "New card",
        card.location
    )

    if (isLoading) {
        Loading()
    } else {
        val conversation = remember(card) {
            card.getConversation()
        }

        Div({
            style {
                flex(1)
                display(DisplayStyle.Flex)
                flexDirection(FlexDirection.Column)
                overflowY("auto")
                overflowX("hidden")
            }
        }) {
            Div({
                style {
                    margin(1.cssRem, 1.cssRem, .5.cssRem, 1.cssRem)
                }
            }) {
                CardPhotoOrVideo(card) {
                    borderRadius(1.cssRem)
                }
            }

            // Todo full conversation support
            if (conversation.message.isNotBlank()) {
                Div({
                    style {
                        padding(1.cssRem)
                        whiteSpace("pre-wrap")
                    }
                }) {
                    Text(conversation.message)
                }
            }

            NavSearchInput(newCardTitle, { newCardTitle = it }, placeholder = "New card", autoFocus = false) {
                newSubCard(card, it)
                newCardTitle = ""
            }

            Div(
                {
                    classes(CardsPageStyles.layout)
                    style {
                        paddingBottom(1.cssRem)
                    }
                }
            ) {
                cards.forEach { card ->
                    CardItem(card) {
                        onCard(card)
                    }
                }
            }
        }
    }
}
