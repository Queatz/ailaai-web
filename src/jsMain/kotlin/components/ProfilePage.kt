package components

import Card
import PaddingDefault
import PersonProfile
import Styles
import androidx.compose.runtime.*
import app.softwork.routingcompose.Router
import baseUrl
import http
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.browser.window
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Source
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.dom.Video
import org.w3c.dom.HTMLVideoElement
import profile.ProfileStyles
import kotlin.js.Date

@Composable
fun ProfilePage(personId: String, onProfile: (PersonProfile) -> Unit) {
    Style(ProfileStyles)

    val scope = rememberCoroutineScope()
    val router = Router.current
    var profile by remember { mutableStateOf<PersonProfile?>(null) }
    var cards by remember { mutableStateOf<List<Card>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isLoading = true
        try {
            profile = http.get("$baseUrl/people/$personId/profile").body()
            onProfile(profile!!)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        try {
            cards = http.get("$baseUrl/people/$personId/profile/cards").body()
        } catch (e: Exception) {
            e.printStackTrace()
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
            Text("Profile not found.")
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
                                    property("aspect-ratio", "2")
                                }
                            }) {}
                        } ?: profile.profile.video?.let {
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
                                    var unmute by remember { mutableStateOf(false) }
                                    videoEl.onloadedmetadata = {
                                        videoEl.muted = true
                                        unmute = true
                                        it
                                    }
                                    LaunchedEffect(unmute) {
                                        if (unmute) {
                                            delay(250)
                                            try {
                                                videoEl.muted = false
                                            } catch (e: Exception) {
                                                // ignore
                                            }
                                        }
                                    }
                                    onDispose {  }
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
                                    classes(ProfileStyles.photo)
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
                                        Div({ classes(ProfileStyles.infoCardName) }) { Text("Friends") }
                                    }
                                    Div({
                                        classes(ProfileStyles.infoCard)
                                    }) {
                                        Div({ classes(ProfileStyles.infoCardValue) }) { Text("${profile.stats.cardCount}") }
                                        Div({ classes(ProfileStyles.infoCardName) }) { Text("Cards") }
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
                                        Div({ classes(ProfileStyles.infoCardName) }) { Text("Joined") }
                                    }
                                }
                                Div({
                                    style {
                                        lineHeight("1.5")
                                    }
                                }) {
                                    Text(profile.profile.about ?: "")
                                }
                            }
                        }
                    }
                }
                Div({
                    classes(Styles.content)
                }) {
                    cards.forEach { card ->
                        CardItem(card, router)
                    }
                }
            }
        }
    }
}
