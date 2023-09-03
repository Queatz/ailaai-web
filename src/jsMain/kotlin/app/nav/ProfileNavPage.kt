package app.nav

import LocalConfiguration
import PaddingDefault
import Person
import PersonProfile
import Profile
import androidx.compose.runtime.*
import api
import appString
import application
import components.IconButton
import dialog
import inputDialog
import kotlinx.browser.window
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import notBlank
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.events.Event
import qr
import webBaseUrl

@Composable
fun ProfileNavPage(onProfileClick: () -> Unit) {
    val me by application.me.collectAsState()
    val scope = rememberCoroutineScope()

    var profile by remember {
        mutableStateOf<PersonProfile?>(null)
    }

    suspend fun reload() {
        api.profile(me!!.id!!) {
            profile = it
        }
    }

    LaunchedEffect(me) {
        if (me != null) {
            reload()
        }
    }

    var messageText by remember(profile) {
        mutableStateOf(profile?.profile?.about ?: "")
    }

    var messageChanged by remember(profile) { mutableStateOf(false) }
    var isSaving by remember(profile) { mutableStateOf(false) }


    fun saveAbout() {
        isSaving = true

        scope.launch {
            api.updateProfile(Profile(about = messageText)) {
                messageChanged = false
                reload()
            }
            isSaving = false
        }
    }

    NavTopBar(me, "Profile", onProfileClick = onProfileClick) {
        IconButton("qr_code", "QR code", styles = {
        }) {
            scope.launch {
                dialog("", cancelButton = null) {
                    val qrCode = remember {
                        "$webBaseUrl/profile/${me!!.id!!}".qr
                    }
                    Img(src = qrCode) {
                        style {
                            borderRadius(1.cssRem)
                        }
                    }
                }
            }
        }

        IconButton("open_in_new", appString { viewProfile }, styles = {
            marginRight(.5.cssRem)
        }) {
            window.open("/profile/${me!!.id!!}", "_blank")
        }
    }

    Div({
        style {
            overflowY("auto")
            overflowX("hidden")
            padding(PaddingDefault / 2)
            display(DisplayStyle.Flex)
            flexDirection(FlexDirection.Column)
        }
    }) {
        NavMenuItem("account_circle", me?.name?.notBlank ?: "Your name") {
            scope.launch {
                val name = inputDialog("Your name", confirmButton = "Update", defaultValue = me?.name ?: "")

                api.updateMe(Person(name = name)) {
                    application.setMe(it)
                }
            }
        }

        val configuration = LocalConfiguration.current

        NavMenuItem(
            when (configuration.language) {
                "vi" -> "\uD83C\uDDFB\uD83C\uDDF3"
                "ru" -> "\uD83C\uDDF7\uD83C\uDDFA"
                else -> "\uD83C\uDDEC\uD83C\uDDE7"
            },
            when (configuration.language) {
                "vi" -> "Language"
                "ru" -> "Язык"
                else -> "Ngôn ngữ"
            },
            textIcon = true
        ) {
            configuration.set(
                when (configuration.language) {
                    "en" -> "vi"
                    //"vi" -> "ru"
                    else -> "en"
                }
            )
        }

        if (profile != null) {
            var onValueChange by remember { mutableStateOf({}) }

            LaunchedEffect(messageText) {
                onValueChange()
            }

            TextArea(messageText) {
                classes(Styles.textarea)
                style {
                    margin(.5.cssRem)
                    height(3.5.cssRem)
                    maxHeight(18.cssRem)
                    flexShrink(0)
                    backgroundColor(Color.transparent)
                }

                placeholder("Details")

                onKeyDown {
                    if (it.key == "Enter" && it.ctrlKey) {
                        it.preventDefault()
                        it.stopPropagation()
                        saveAbout()
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
                        margin(.5.cssRem)
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
                            saveAbout()
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
                            messageText = profile?.profile?.about ?: ""
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
        }
    }
}
