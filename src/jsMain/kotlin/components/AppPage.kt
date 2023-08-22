package components

import GroupExtended
import Styles
import androidx.compose.runtime.*
import app.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div

@Composable
fun AppPage() {
    Style(AppStyles)

    var nav by remember {
        mutableStateOf("groups")
    }

    var group by remember {
        mutableStateOf<GroupExtended?>(null)
    }

    Div({
        classes(AppStyles.baseLayout)
    }) {
        Div({
            classes(AppStyles.sideLayout)
        }) {
            Div({
                classes(AppStyles.bottomBar)
                style {
                    flexShrink(0)
                }
            }) {
                IconButton("people", "Groups", styles = {
                    if (nav == "groups") {
                        backgroundColor(Styles.colors.primary)
                        color(Color.white)
                    }
                }) {
                    nav = "groups"
                }
                IconButton("calendar_today", "Schedule", styles = {
                    if (nav == "schedule") {
                        backgroundColor(Styles.colors.primary)
                        color(Color.white)
                    }
                }) {
                    nav = "schedule"
                }
                IconButton("style", "Cards", styles = {
                    if (nav == "cards") {
                        backgroundColor(Styles.colors.primary)
                        color(Color.white)
                    }
                }) {
                    nav = "cards"
                }
                IconButton("feed", "Stories", styles = {
                    if (nav == "stories") {
                        backgroundColor(Styles.colors.primary)
                        color(Color.white)
                    }
                }) {
                    nav = "stories"
                }
            }
            Div({
                style {
                    flexGrow(1)
                    display(DisplayStyle.Flex)
                    flexDirection(FlexDirection.Column)
                    overflow("hidden")
                }
            }) {
                when (nav) {
                    "groups" -> GroupsNavPage(
                        group,
                        onGroupSelected = {
                            group = it
                        }
                    )
                    "schedule" -> ScheduleNavPage()
                    "cards" -> CardsNavPage()
                    "stories" -> StoriesNavPage()
                }
            }
        }
        Div({
            classes(AppStyles.mainLayout)
        }) {
            when (nav) {
                "groups" -> {
                    GroupPage(group)
                }
            }
        }
    }
}
