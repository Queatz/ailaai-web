package app.page

import Card
import SaveAndCard
import Styles
import androidx.compose.runtime.*
import app.FullPageLayout
import app.PageTopBar
import app.menu.Menu
import app.nav.CardNav
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
import json
import kotlinx.browser.window
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import notBlank
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.dom.TextArea
import org.w3c.dom.DOMRect
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.Event
import saves

@Composable
fun CardsPage(nav: CardNav, onCard: (CardNav) -> Unit, onCardUpdated: (Card) -> Unit) {
    Style(CardsPageStyles)
    val me by application.me.collectAsState()
    var cards by remember(nav) {
        mutableStateOf(listOf<Card>())
    }
    var isLoading by remember(nav !is CardNav.Selected) {
        mutableStateOf(nav !is CardNav.Selected)
    }

    suspend fun reload() {
        if (me == null) return

        when (nav) {
            is CardNav.Saved -> {
                try {
                    cards = http.get("$baseUrl/me/saved") {
                        contentType(ContentType.Application.Json.withCharset(Charsets.UTF_8))
                        bearerAuth(application.bearer)
                    }.body<List<SaveAndCard>>().mapNotNull { it.card }
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }

            else -> {
                try {
                    cards = http.get("$baseUrl/cards") {
                        parameter("geo", me?.geo?.joinToString(",") ?: "10.7915858,106.7426523") // todo
                        contentType(ContentType.Application.Json.withCharset(Charsets.UTF_8))
                        bearerAuth(application.bearer)
                    }.body()
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
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
            } else {
                MyCardPage(nav.card, {
                    onCard(CardNav.Selected(it))
                }, onCardUpdated = {
                    onCardUpdated(it)
                })
            }
        }
    }
}

@Composable
fun MyCardPage(card: Card, onCard: (Card) -> Unit, onCardUpdated: (Card) -> Unit) {
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

    var menuTarget by remember {
        mutableStateOf<DOMRect?>(null)
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
                onCardUpdated(card)
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    if (menuTarget != null) {
        Menu({ menuTarget = null }, menuTarget!!) {
            val isSaved = saves.cards.value.any { it.id == card.id }
            item(if (isSaved) "Unsave" else "Save") {
                scope.launch {
                    if (isSaved) {
                        saves.unsave(card.id!!)
                    } else {
                        saves.save(card.id!!)
                    }
                }
            }
            item("Rename") {
                scope.launch {
                    val name = window.prompt("Title", card.name ?: "")

                    if (name == null) {
                        return@launch
                    }

                    try {
                        val card = http.post("$baseUrl/cards/${card.id!!}") {
                            setBody(Card(name = name))
                            contentType(ContentType.Application.Json.withCharset(Charsets.UTF_8))
                            bearerAuth(application.bearer)
                        }.body<Card>()
                        onCardUpdated(card)
                    } catch (e: Throwable) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    PageTopBar(
        card.name?.notBlank ?: "New card",
        card.location
    ) {
        menuTarget = if (menuTarget == null) (it.target as HTMLElement).getBoundingClientRect() else null
    }

    val conversation = remember(card) {
        card.getConversation()
    }

    var messageText by remember(conversation) {
        mutableStateOf(conversation.message)
    }

    var messageChanged by remember(card) {
        mutableStateOf(false)
    }

    var isSaving by remember(card) {
        mutableStateOf(false)
    }

    fun saveConversation() {
        conversation.message = messageText
        val conversationString = json.encodeToString(conversation)

        isSaving = true

        scope.launch {
            try {
                val card = http.post("$baseUrl/cards/${card.id!!}") {
                    setBody(Card(conversation = conversationString))
                    contentType(ContentType.Application.Json.withCharset(Charsets.UTF_8))
                    bearerAuth(application.bearer)
                }.body<Card>()
                messageChanged = false
                onCardUpdated(card)
            } catch (e: Throwable) {
                e.printStackTrace()
            }

            isSaving = false
        }
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
        if (card.photo != null || card.video != null) {
            Div({
                style {
                    margin(1.cssRem, 1.cssRem, .5.cssRem, 1.cssRem)
                }
            }) {
                CardPhotoOrVideo(card) {
                    borderRadius(1.cssRem)
                }
            }
        }

        // Todo full conversation support

//            if (conversation.message.isNotBlank()) {
//                Div({
//                    style {
//                        padding(1.cssRem)
//                        whiteSpace("pre-wrap")
//                    }
//                }) {
//                    Text(conversation.message)
//                }
//            }

        var onValueChange by remember { mutableStateOf({}) }

        LaunchedEffect(messageText) {
            onValueChange()
        }

        TextArea(messageText) {
            classes(Styles.textarea)
            style {
                margin(.5.cssRem, 1.cssRem)
                height(3.5.cssRem)
                maxHeight(50.vh)
                flexShrink(0)
                backgroundColor(Color.transparent)
            }

            placeholder("Details")

            onKeyDown {
                if (it.key == "Enter" && it.ctrlKey) {
                    it.preventDefault()
                    it.stopPropagation()
                    saveConversation()
                }
            }

            onInput {
                messageText = it.value
                it.target.style.height = "0"
                it.target.style.height = "${it.target.scrollHeight + 2}px"
                messageChanged = true
            }

            onChange {
                it.target.style.height = "0"
                it.target.style.height = "${it.target.scrollHeight + 2}px"
            }

            ref { element ->
                element.style.height = "0"
                element.style.height = "${element.scrollHeight + 2}px"

                onValueChange = { element.dispatchEvent(Event("change")) }

                onDispose {
                    onValueChange = {}
                }
            }
        }

        if (messageChanged) {
            Div({
                style {
                    margin(.5.cssRem, 1.cssRem)
                    flexShrink(0)
                    display(DisplayStyle.Flex)
                }
            }) {
                Button({
                    classes(Styles.button)

                    style {
                        marginRight(.5.cssRem)
                    }

                    onClick {
                        saveConversation()
                    }

                    if (isSaving) {
                        disabled()
                    }
                }) {
                    Text("Save")
                }

                Button({
                    classes(Styles.outlineButton)
                    style {
                        marginRight(.5.cssRem)
                    }
                    onClick {
                        messageText = conversation.message
                        messageChanged = false
                    }

                    if (isSaving) {
                        disabled()
                    }
                }) {
                    Text("Discard")
                }
            }
        }

        NavSearchInput(newCardTitle, { newCardTitle = it }, placeholder = "New card", autoFocus = false) {
            newSubCard(card, it)
            newCardTitle = ""
        }

        if (isLoading) {
            Loading()
        } else {
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
