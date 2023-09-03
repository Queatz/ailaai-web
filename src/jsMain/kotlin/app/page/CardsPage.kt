package app.page

import Card
import Styles
import androidx.compose.runtime.*
import api
import app.FullPageLayout
import app.PageTopBar
import app.menu.Menu
import app.nav.CardNav
import app.nav.NavSearchInput
import application
import components.*
import dialog
import inputDialog
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.util.*
import json
import kotlinx.browser.window
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import notBlank
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.DOMRect
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.Event
import pickPhotos
import qr
import saves
import toScaledBytes
import webBaseUrl

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

@OptIn(InternalAPI::class)
@Composable
fun MyCardPage(card: Card, onCard: (Card) -> Unit, onCardUpdated: (Card) -> Unit, onCardDeleted: (card: Card) -> Unit) {
    val me by application.me.collectAsState()
    val scope = rememberCoroutineScope()

    var newCardTitle by remember(card) {
        mutableStateOf("")
    }

    var cards by remember(card.id) {
        mutableStateOf(listOf<Card>())
    }

    var isLoading by remember(card.id) {
        mutableStateOf(true)
    }

    var menuTarget by remember {
        mutableStateOf<DOMRect?>(null)
    }

    suspend fun reload() {
        if (me == null) return
        api.cardsOfCard(card.id!!) {
            cards = it
        }
        isLoading = false
    }

    LaunchedEffect(card.id) {
        reload()
    }

    fun newSubCard(inCard: Card, name: String) {
        scope.launch {
            api.newCard(Card(name = name, parent = inCard.id!!)) {
                reload()
                onCardUpdated(it)
            }
        }
    }

    if (menuTarget != null) {
        Menu({ menuTarget = null }, menuTarget!!) {
            val isSaved = saves.cards.value.any { it.id == card.id }
            item("Open in new tab", icon = "open_in_new") {
                window.open("/page/${card.id}", target = "_blank")
            }
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
                    val name = inputDialog(
                        "Page title",
                        "",
                        "Rename",
                        defaultValue = card.name ?: ""
                    )

                    if (name == null) {
                        return@launch
                    }

                    api.updateCard(card.id!!, Card(name = name)) {
                        onCardUpdated(it)
                    }
                }
            }

            item("Hint") {
                scope.launch {
                    val hint = inputDialog(
                        "Page hint",
                        "",
                        "Update",
                        defaultValue = card.location ?: ""
                    )

                    if (hint == null) {
                        return@launch
                    }

                    api.updateCard(card.id!!, Card(location = hint)) {
                        onCardUpdated(it)
                    }
                }
            }

            item("Choose photo") {
                pickPhotos(multiple = false) {
                    it.singleOrNull()?.let {
                        scope.launch {
                            val photo = it.toScaledBytes()
                            api.updateCardPhoto(
                                card.id!!,
                                MultiPartFormDataContent(
                                    formData {
                                        append(
                                            "photo",
                                            photo,
                                            Headers.build {
                                                append(HttpHeaders.ContentType, "image/jpeg")
                                                append(HttpHeaders.ContentDisposition, "filename=photo.jpg")
                                            }
                                        )
                                    }
                                )
                            ) {
                                onCardUpdated(card)
                            }
                        }
                    }
                }
            }
            if (card.parent != null) {
                item("Open enclosing page") {
                    scope.launch {
                        api.card(card.parent!!) {
                            onCard(it)
                        }
                    }
                }
            }

            item("QR code") {
                scope.launch {
                    dialog("", cancelButton = null) {
                        val qrCode = remember {
                            "$webBaseUrl/card/${card.id!!}".qr
                        }
                        Img(src = qrCode) {
                            style {
                                borderRadius(1.cssRem)
                            }
                        }
                    }
                }
            }

            item("Delete") {
                scope.launch {
                    val result = dialog(
                        "Delete this page?",
                        "Yes, delete"
                    ) {
                        Text("You cannot undo this.")
                    }

                    if (result == true) {
                        api.deleteCard(card.id!!) {
                            onCardDeleted(card)
                        }
                    }
                }
            }
        }
    }

    var published by remember(card) {
        mutableStateOf(card.active == true)
    }

    PageTopBar(
        card.name?.notBlank ?: "New page",
        card.location,
        actions = {
            Switch(published, { published = it }, {
                scope.launch {
                    val previousValue = card.active == true
                    api.updateCard(card.id!!, Card(active = it), onError = {
                        published = previousValue
                    }) {
                        onCardUpdated(it)
                    }
                }
            }, title = "Page is ${if (published) "published" else "not published"}") {
                margin(1.cssRem)
            }
        }
    ) {
        menuTarget = if (menuTarget == null) (it.target as HTMLElement).getBoundingClientRect() else null
    }

    val conversation = remember(card) {
        card.getConversation()
    }

    var messageText by remember(conversation) {
        mutableStateOf(conversation.message)
    }

    var messageChanged by remember(card.id) {
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
            api.updateCard(card.id!!, Card(conversation = conversationString)) {
                messageChanged = false
                onCardUpdated(card)
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

        NavSearchInput(newCardTitle, { newCardTitle = it }, placeholder = "New page", autoFocus = false) {
            if (newCardTitle.isNotBlank()) {
                newSubCard(card, it)
                newCardTitle = ""
            }
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
                    CardItem(card, showTapToOpen = false) {
                        onCard(card)
                    }
                }
            }
        }
    }
}
