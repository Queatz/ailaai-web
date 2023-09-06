package app

import Card
import GroupExtended
import Story
import androidx.compose.runtime.*
import app.cards.CardsPage
import app.nav.*
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
    Stories,
    Profile
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
        mutableStateOf<CardNav>(CardNav.Explore)
    }

    var reminder by remember {
        mutableStateOf<String?>(null)
    }

    val cardUpdates = remember {
        MutableSharedFlow<Card>()
    }

    val groupUpdates = remember {
        MutableSharedFlow<Unit>()
    }

    val storyUpdates = remember {
        MutableSharedFlow<Story>()
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
                        },
                        onProfileClick = {
                            nav = NavPage.Profile
                        }
                    )

                    NavPage.Schedule -> ScheduleNavPage(reminder, { reminder = it }, scheduleView, {
                        scheduleView = it
                    }, {
                        nav = NavPage.Profile
                    })

                    NavPage.Cards -> CardsNavPage(cardUpdates, card, { card = it }, {
                        nav = NavPage.Profile
                    })
                    NavPage.Stories -> StoriesNavPage(storyUpdates, story, { story = it }, { nav = NavPage.Profile })
                    NavPage.Profile -> ProfileNavPage {
                        nav = NavPage.Groups
                    }
                }
            }
        }
        Div({
            classes(AppStyles.mainLayout)
        }) {
            when (nav) {
                NavPage.Groups -> GroupPage(
                    group,
                    onGroupUpdated = {
                        scope.launch {
                            groupUpdates.emit(Unit)
                        }
                    },
                    onGroupGone = {
                        scope.launch {
                            groupUpdates.emit(Unit)
                        }
                    }
                )

                NavPage.Schedule -> SchedulePage(scheduleView, reminder, { reminder = it })
                NavPage.Cards -> CardsPage(card, { card = it }) {
                    scope.launch {
                        cardUpdates.emit(it)
                    }
                }

                NavPage.Stories -> StoriesPage(story) {
                    scope.launch {
                        storyUpdates.emit(it)
                    }
                }
                NavPage.Profile -> {

                }
            }
        }
    }
}
