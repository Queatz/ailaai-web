package app.page

import GroupExtended
import GroupLayout
import androidx.compose.runtime.*
import api
import app.group.GroupList
import app.nav.GroupNav
import appText
import application
import components.Loading
import defaultGeo
import notBlank
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div

@Composable
fun GroupPage(nav: GroupNav, onGroupUpdated: () -> Unit, onGroupGone: () -> Unit) {
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
                api.publicGroups(
                    geo = me?.geo ?: defaultGeo,
                    search = search.notBlank,
                    public = false
                ) {
                    groups = it
                }
                isLoading = false
            }

            GroupNav.Local -> {
                isLoading = true
                api.publicGroups(
                    geo = me?.geo ?: defaultGeo,
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
        GroupList(groups, public = false)
    } else if (nav is GroupNav.Local) {
        GroupList(groups, public = true)
    } else if (nav is GroupNav.Selected) {
        GroupLayout(nav.group, onGroupUpdated, onGroupGone)
    }
}
