package app.cards

import Card
import Configuration
import LocalConfiguration
import androidx.compose.runtime.*
import api
import app.PageTopBar
import app.components.EditField
import app.menu.InlineMenu
import app.menu.Menu
import app.nav.NavSearchInput
import application
import components.*
import dialog
import inputDialog
import io.ktor.client.request.forms.*
import io.ktor.http.*
import json
import kotlinx.browser.window
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import notBlank
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Br
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.DOMRect
import org.w3c.dom.HTMLElement
import pickPhotos
import qr
import r
import saves
import toScaledBytes
import webBaseUrl

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

    fun generatePhoto() {
        scope.launch {
            api.generateCardPhoto(card.id!!) {
                dialog("Generating", cancelButton = null) {
                    Div {
                        Text("The page will be updated when the photo is generated.")
                        Br()
                        Br()
                        Text("Page title, hint, and details are shared with stability.ai.")
                    }
                }
            }
        }
    }

    val configuration = LocalConfiguration.current

    fun moveToPage(cardId: String) {
        scope.launch {
            api.updateCard(card.id!!, Card(offline = false, parent = cardId, equipped = false, geo = null)) {
                onCardUpdated(it)
            }
        }
    }

    fun moveToPage() {
        scope.launch {
            val result = dialog("In a page", "Cancel", null) { resolve ->
                var value by remember {
                    mutableStateOf("")
                }
                var loading by remember {
                    mutableStateOf(true)
                }

                var allCards by remember { mutableStateOf(emptyList<Card>()) }
                var cards by remember { mutableStateOf(emptyList<Card>()) }
                val saved by saves.cards.collectAsState()

                LaunchedEffect(Unit) {
                    api.collaborations {
                        allCards = it
                    }
                    loading = false
                }

                LaunchedEffect(allCards, value) {
                    cards = if (value.isBlank()) {
                        allCards
                    } else {
                        allCards.filter { (it.name?.contains(value, true) ?: false) }
                    }.filter {
                        it.id != card.id
                    }.sortedByDescending { saved.any { save -> it.id == save.id } }
                }

                CompositionLocalProvider(LocalConfiguration provides configuration) {
                    NavSearchInput(
                        value,
                        {
                            value = it
                        },
                        placeholder = "Search",
                        styles = {
                            margin(0.r)
                        },
                        onDismissRequest = {
                            resolve(false)
                        }
                    ) {
                        resolve(true)
                    }

                    if (loading) {
                        Div({
                            style {
                                padding(1.r)
                            }
                        }) {
                            Loading()
                        }
                    } else {
                        Div({
                            style {
                                overflowY("auto")
                                overflowX("hidden")
                                width(28.r)
                                maxWidth(100.percent)
                                padding(1.r / 2, 0.r)
                            }
                        }) {
                            cards.forEach { card ->
                                app.nav.CardItem(
                                    card,
                                    false,
                                    false,
                                    saved.any { save -> save.id == card.id },
                                    card.active == true
                                ) {
                                    if (!it) {
                                        moveToPage(card.id!!)
                                        resolve(false)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (result == true) {
                if (cards.isNotEmpty()) {
                    moveToPage(cards.first().id!!)
                }
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

            item("Location") {
                scope.launch {
                    val name = dialog(
                        "Location",
                        "Close",
                        null
                    ) {
                        InlineMenu({
                            it(true)
                        }) {
                            item("On profile", selected = card.equipped == true, "account_circle") {
                                scope.launch {
                                    api.updateCard(card.id!!, Card(offline = false, parent = null, equipped = true, geo = null)) {
                                        onCardUpdated(it)
                                    }
                                }
                            }
                            item("At a location", selected = card.parent == null && card.offline != true && card.equipped != true && card.geo != null, "location_on") {

                            }
                            item("In a page", selected = card.parent != null, "description") {
                                moveToPage()
                            }
                            item("None", selected = card.offline == true) {
                                scope.launch {
                                    api.updateCard(card.id!!, Card(offline = true, parent = null, equipped = false, geo = null)) {
                                        onCardUpdated(it)
                                    }
                                }
                            }
                        }
                    }

                    if (name == null) {
                        return@launch
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

            if (card.video == null) {
                item(if (card.photo == null) "Generate photo" else "Regenerate photo") {
                    if (card.photo == null) {
                        generatePhoto()
                    } else {
                        scope.launch {
                            val result = dialog("Generate a new photo?") {
                                Text("This will replace the current photo.")
                            }

                            if (result == true) {
                                generatePhoto()
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
                                borderRadius(1.r)
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
                margin(1.r)
            }
        }
    ) {
        menuTarget = if (menuTarget == null) (it.target as HTMLElement).getBoundingClientRect() else null
    }

    val conversation = remember(card) {
        card.getConversation()
    }

    suspend fun saveConversation(value: String): Boolean {
        conversation.message = value
        val conversationString = json.encodeToString(conversation)

        var success = false

        api.updateCard(card.id!!, Card(conversation = conversationString)) {
            success = true
            onCardUpdated(card)
        }

        return success
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
                    margin(1.r, 1.r, .5.r, 1.r)
                }
            }) {
                CardPhotoOrVideo(card) {
                    borderRadius(1.r)
                }
            }
        }

        // Todo full conversation support

//            if (conversation.message.isNotBlank()) {
//                Div({
//                    style {
//                        padding(1.r)
//                        whiteSpace("pre-wrap")
//                    }
//                }) {
//                    Text(conversation.message)
//                }
//            }

        EditField(conversation.message, placeholder = "Details", styles = {
            margin(.5.r, 1.r)
            maxHeight(50.vh)
        }) {
            saveConversation(it)
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
                        paddingBottom(1.r)
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
