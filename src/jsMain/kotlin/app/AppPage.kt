package app

import Card
import GroupExtended
import Story
import androidx.compose.runtime.*
import app.nav.CardsNavPage
import app.nav.GroupsNavPage
import app.nav.ScheduleNavPage
import app.nav.StoriesNavPage
import app.page.*
import application
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div

@Serializable
enum class NavPage {
    Groups,
    Schedule,
    Cards,
    Stories
}

@Composable
fun AppPage() {
    Style(AppStyles)

    val scope = rememberCoroutineScope()

    var nav by remember {
        mutableStateOf(application.navPage)
    }

    var group by remember {
        mutableStateOf<GroupExtended?>(null)
    }

    var card by remember {
        mutableStateOf<Card?>(null)
    }

    val cardUpdates = remember {
        MutableSharedFlow<Card>()
    }

    val groupUpdates = remember {
        MutableSharedFlow<Unit>()
    }

    var story by remember {
        mutableStateOf<Story?>(null)
    }

    var scheduleView by remember {
        mutableStateOf(ScheduleView.Monthly)
    }

    LaunchedEffect(nav) {
        application.setNavPage(nav)
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
                        groupUpdates,
                        group,
                        onGroupSelected = {
                            group = it
                        }
                    )
                    NavPage.Schedule -> ScheduleNavPage(scheduleView) {
                        scheduleView = it
                    }
                    NavPage.Cards -> CardsNavPage(cardUpdates, card) { card = it }
                    NavPage.Stories -> StoriesNavPage(story) { story = it }
                }
            }
        }
        Div({
            classes(AppStyles.mainLayout)
        }) {
            when (nav) {
                NavPage.Groups -> GroupPage(group) {
                    scope.launch {
                        groupUpdates.emit(Unit)
                    }
                }
                NavPage.Schedule -> SchedulePage(scheduleView)
                NavPage.Cards -> CardsPage(card, { card = it }) {
                    scope.launch {
                        cardUpdates.emit(it)
                    }
                }
                NavPage.Stories -> StoriesPage(story)
            }
        }
    }
}
