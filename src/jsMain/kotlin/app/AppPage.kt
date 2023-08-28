package app

import GroupExtended
import Styles
import androidx.compose.runtime.*
import app.nav.CardsNavPage
import app.nav.GroupsNavPage
import app.nav.ScheduleNavPage
import app.nav.StoriesNavPage
import app.page.CardsPage
import app.page.GroupPage
import app.page.SchedulePage
import app.page.StoriesPage
import components.IconButton
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div

private enum class NavPage {
    Groups,
    Schedule,
    Cards,
    Stories
}

@Composable
fun AppPage() {
    Style(AppStyles)

    var nav by remember {
        mutableStateOf(NavPage.Groups)
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
                    if (nav == NavPage.Groups) {
                        backgroundColor(Styles.colors.primary)
                        color(Color.white)
                    }
                }) {
                    nav = NavPage.Groups
                }
                IconButton("calendar_today", "Schedule", styles = {
                    if (nav == NavPage.Schedule) {
                        backgroundColor(Styles.colors.primary)
                        color(Color.white)
                    }
                }) {
                    nav = NavPage.Schedule
                }
                IconButton("style", "Cards", styles = {
                    if (nav == NavPage.Cards) {
                        backgroundColor(Styles.colors.primary)
                        color(Color.white)
                    }
                }) {
                    nav = NavPage.Cards
                }
                IconButton("feed", "Stories", styles = {
                    if (nav == NavPage.Stories) {
                        backgroundColor(Styles.colors.primary)
                        color(Color.white)
                    }
                }) {
                    nav = NavPage.Stories
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
                    NavPage.Groups -> GroupsNavPage(
                        group,
                        onGroupSelected = {
                            group = it
                        }
                    )
                    NavPage.Schedule -> ScheduleNavPage()
                    NavPage.Cards -> CardsNavPage()
                    NavPage.Stories -> StoriesNavPage()
                }
            }
        }
        Div({
            classes(AppStyles.mainLayout)
        }) {
            when (nav) {
                NavPage.Groups -> GroupPage(group)
                NavPage.Schedule -> SchedulePage()
                NavPage.Cards -> CardsPage()
                NavPage.Stories -> StoriesPage()
            }
        }
    }
}
