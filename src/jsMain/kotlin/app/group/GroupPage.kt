package app.group

import GroupLayout
import com.queatz.db.*
import androidx.compose.runtime.*
import api
import app.FullPageLayout
import app.ailaai.api.exploreGroups
import app.components.Empty
import app.nav.GroupNav
import appText
import application
import components.Loading
import defaultGeo
import notBlank
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import r

@Composable
fun GroupPage(
    nav: GroupNav,
    onGroup: (GroupExtended) -> Unit,
    onGroupUpdated: () -> Unit,
    onGroupGone: () -> Unit
) {
    val me by application.me.collectAsState()

    var isLoading by remember {
        mutableStateOf(true)
    }

    var groups by remember {
        mutableStateOf(emptyList<GroupExtended>())
    }

    var search by remember {
        mutableStateOf("")
    }

    LaunchedEffect(nav, search) {
        groups = emptyList()
        when (nav) {
            GroupNav.Friends -> {
                isLoading = true
                api.exploreGroups(
                    geo = me?.geo?.asGeo() ?: defaultGeo,
                    search = search.notBlank,
                    public = false
                ) {
                    groups = it
                }
                isLoading = false
            }

            GroupNav.Local -> {
                isLoading = true
                api.exploreGroups(
                    geo = me?.geo?.asGeo() ?: defaultGeo,
                    search = search.notBlank,
                    public = true
                ) {
                    groups = it
                }
                isLoading = false
            }

            else -> {
                isLoading = false
            }
        }
    }

    if (nav == GroupNav.None) {
        Div({
            style {
                height(100.percent)
                display(DisplayStyle.Flex)
                alignItems(AlignItems.Center)
                justifyContent(JustifyContent.Center)
                opacity(.5)
            }
        }) {
            appText { selectAGroup }
        }
    } else if (isLoading) {
        Loading()
    } else if (nav is GroupNav.Friends) {
        FullPageLayout {
            Div({
                style {
                    display(DisplayStyle.Flex)
                    flexDirection(FlexDirection.Column)
                    padding(1.r)
                }
            }) {
                if (groups.isEmpty()) {
                    Empty {
                        appText { noGroups }
                    }
                } else {
                    GroupList(groups) {
                        onGroup(it)
                    }
                }
            }
        }
    } else if (nav is GroupNav.Local) {
        FullPageLayout {
            Div({
                style {
                    display(DisplayStyle.Flex)
                    flexDirection(FlexDirection.Column)
                    padding(1.r)
                }
            }) {
                if (groups.isEmpty()) {
                    Empty {
                        appText { noGroups }
                    }
                } else {
                    GroupList(groups) {
                        onGroup(it)
                    }
                }
            }
        }
    } else if (nav is GroupNav.Selected) {
        GroupLayout(nav.group, onGroupUpdated, onGroupGone)
    }
}
