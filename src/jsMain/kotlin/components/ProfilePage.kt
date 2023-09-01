package components

import Card
import PaddingDefault
import PersonProfile
import Styles
import androidx.compose.runtime.*
import api
import app.softwork.routingcompose.Router
import appString
import baseUrl
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Source
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.dom.Video
import org.w3c.dom.HTMLVideoElement
import profile.ProfileStyles
import kotlin.js.Date

@Composable
fun ProfilePage(personId: String? = null, url: String? = null, onProfile: (PersonProfile) -> Unit) {
    Style(ProfileStyles)

    var personId by remember { mutableStateOf(personId) }
    val scope = rememberCoroutineScope()
    val router = Router.current
    var profile by remember { mutableStateOf<PersonProfile?>(null) }
    var cards by remember { mutableStateOf<List<Card>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    if (personId == null && url == null) {
        router.navigate("/")
        return
    }

    LaunchedEffect(Unit) {
        isLoading = true
        if (personId != null) {
            api.profile(
                personId!!,
                onError = {
                    // todo show not found page
                    router.navigate("/")
                }
            ) {
                profile = it
                onProfile(profile!!)

                if (personId == null) {
                    personId = profile!!.person.id
                }
            }
        } else {
            api.profileByUrl(
                url!!,
                onError = {
                    // todo show not found page
                    router.navigate("/")
                }
            ) {
                profile = it
                onProfile(profile!!)

                if (personId == null) {
                    personId = profile!!.person.id
                }
            }
        }
        isLoading = false
    }

    LaunchedEffect(personId) {
        if (personId != null) {
            api.profileCards(personId!!) {
                cards = it
            }
        }
    }

    if (!isLoading && profile == null) {
        Div({
            classes(Styles.mainContent)
            style {
                display(DisplayStyle.Flex)
                minHeight(100.vh)
                width(100.percent)
                flexDirection(FlexDirection.Column)
                padding(PaddingDefault * 2)
                alignItems(AlignItems.Center)
                justifyContent(JustifyContent.FlexStart)
            }
        }) {
            Text(appString { profileNotFound })
        }
    } else {
        profile?.let { profile ->
            Div({
                classes(Styles.mainContent)
            }) {
                Div({
                    classes(Styles.navContainer)
                }) {
                    Div({
                        classes(Styles.navContent)
                    }) {
                        profile.profile.photo?.let { // photo or video
                            Div({
                                style {
                                    width(100.percent)
                                    backgroundColor(Styles.colors.background)
                                    backgroundImage("url($baseUrl$it)")
                                    backgroundPosition("center")
                                    backgroundSize("cover")
                                    maxHeight(50.vh)
                                    property("aspect-ratio", "2")
                                }
                            }) {}
                        } ?: profile.profile.video?.let {
                            var videoElement by remember { mutableStateOf<HTMLVideoElement?>(null) }
//                            LaunchedEffect(videoElement) {
//                                if (videoElement != null) {
//                                    delay(250)
//                                    try {
////                                        if (window.navigator.getAutoplayPolicy)
//                                        videoElement!!.muted = false
//                                    } catch (e: Throwable) {
//                                        // ignore
//                                    }
//                                }
//                            }
                            Video({
                                attr("autoplay", "")
                                attr("loop", "")
                                attr("playsinline", "")
                                attr("muted", "")
                                style {
                                    property("object-fit", "cover")
                                    width(100.percent)
                                    backgroundColor(Styles.colors.background)
                                    property("aspect-ratio", "2")
                                }
                                onClick {
                                    (it.target as? HTMLVideoElement)?.apply {
                                        play()
                                        muted = false
                                    }
                                }
                                // Do this so that auto-play works on page load, but unmute on page navigation
                                ref { videoEl ->
                                    videoEl.onloadedmetadata = {
                                        videoEl.muted = true
                                        videoElement = videoEl
                                        it
                                    }
                                    onDispose { }
                                }
                            }) {
                                Source({
                                    attr("src", "$baseUrl$it")
                                    attr("type", "video/webm")
                                })
                            }
                        }
                        Div({
                            classes(ProfileStyles.mainContent)
                        }) {
                            profile.person.photo?.let {
                                Div({
                                    if (profile.profile.photo == null && profile.profile.video == null) {
                                        classes(ProfileStyles.photo, ProfileStyles.nophoto)
                                    } else {
                                        classes(ProfileStyles.photo)
                                    }
                                    style {
                                        backgroundImage("url($baseUrl$it)")
                                    }
                                }) {}
                            }
                            Div({
                                classes(Styles.cardContent, ProfileStyles.profileContent)
                            }) {

                                Div({
                                    classes(ProfileStyles.name)
                                }) {
                                    NameAndLocation(profile.person.name, "")
                                }
                                Div({
                                    style {
                                        display(DisplayStyle.Flex)
                                        alignItems(AlignItems.Stretch)
                                        width(100.percent)
                                    }
                                }) {
                                    Div({
                                        classes(ProfileStyles.infoCard)
                                    }) {
                                        Div({ classes(ProfileStyles.infoCardValue) }) { Text("${profile.stats.friendsCount}") }
                                        Div({ classes(ProfileStyles.infoCardName) }) { Text(appString { friends }) }
                                    }
                                    Div({
                                        classes(ProfileStyles.infoCard)
                                    }) {
                                        Div({ classes(ProfileStyles.infoCardValue) }) { Text("${profile.stats.cardCount}") }
                                        Div({ classes(ProfileStyles.infoCardName) }) { Text(appString { this.cards }) }
                                    }
                                    Div({
                                        classes(ProfileStyles.infoCard)
                                    }) {
                                        Div(
                                            {
                                                classes(ProfileStyles.infoCardValue)
                                                title("${Date(profile.person.createdAt!!)}")
                                            }
                                        ) { Text("${Date(profile.person.createdAt!!).getFullYear()}") }
                                        Div({ classes(ProfileStyles.infoCardName) }) { Text(appString { joined }) }
                                    }
                                }
                                if (profile.profile.about != null) {
                                    Div({
                                        classes(ProfileStyles.infoAbout)
                                    }) {
                                        LinkifyText(profile.profile.about!!)
                                    }
                                }
                            }
                        }
                    }
                }
                Div({
                    classes(Styles.content)
                }) {
                    cards.forEach { card ->
                        CardItem(card, styles = {
                            margin(PaddingDefault)
                        })
                    }
                }
            }
        }
    }
}
