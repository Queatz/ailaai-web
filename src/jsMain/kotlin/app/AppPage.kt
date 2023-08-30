package app

import GroupExtended
import Styles
import androidx.compose.runtime.*
import app.nav.CardsNavPage
import app.nav.GroupsNavPage
import app.nav.ScheduleNavPage
import app.nav.StoriesNavPage
import app.page.*
import components.IconButton
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div

enum class NavPage {
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

    var scheduleView by remember {
        mutableStateOf(ScheduleView.Monthly)
    }

    Div({
        classes(AppStyles.baseLayout)
    }) {
        Div({
            classes(AppStyles.sideLayout)
        }) {
            AppBottomBar(nav) { nav = it }
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
                    NavPage.Schedule -> ScheduleNavPage(scheduleView) {
                        scheduleView = it
                    }
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
                NavPage.Schedule -> SchedulePage(scheduleView)
                NavPage.Cards -> CardsPage()
                NavPage.Stories -> StoriesPage()
            }
        }
    }
}
