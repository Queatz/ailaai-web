package app.nav

import Group
import GroupExtended
import Member
import Styles
import androidx.compose.runtime.*
import api
import app.AppStyles
import app.components.Spacer
import app.messaages.preview
import appText
import application
import components.GroupPhoto
import components.IconButton
import components.Loading
import focusable
import inputDialog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import lib.formatDistanceToNow
import notBlank
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import push
import r
import kotlin.js.Date

sealed class GroupNav {
    data object None : GroupNav()
    data object Friends : GroupNav()
    data object Local : GroupNav()
    data object Saved : GroupNav()
    data class Selected(val group: GroupExtended) : GroupNav()
}

@Composable
fun GroupsNavPage(
    groupUpdates: Flow<Unit>,
    selected: GroupNav,
    onSelected: (GroupNav) -> Unit,
    onProfileClick: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val me by application.me.collectAsState()
    var isLoading by remember {
        mutableStateOf(true)
    }

    var groups by remember {
        mutableStateOf(emptyList<GroupExtended>())
    }

    var showSearch by remember {
        mutableStateOf(false)
    }

    var searchText by remember {
        mutableStateOf("")
    }

    LaunchedEffect(selected) {
        searchText = ""
        showSearch = false
    }

    val shownGroups = remember(groups, searchText) {
        val search = searchText.trim()
        if (searchText.isBlank()) {
            groups
        } else {
            groups.filter {
                (it.group?.name?.contains(search, true)
                    ?: false) || (it.members?.any { it.person?.name?.contains(search, true) ?: false } ?: false)
            }
        }
    }

    suspend fun reload() {
        // todo is next line needed? it was added to wait for the token since groups are the first thing to appear after signing in
        application.bearerToken.first { it != null }
        api.groups {
            groups = it
            groups.firstOrNull { it.group?.id == (selected as? GroupNav.Selected)?.group?.group?.id }?.let {
                onSelected(GroupNav.Selected(it))
            }
        }
    }

    // todo remove selectedGroup
    LaunchedEffect(selected) {
        push.events.collectLatest {
            reload()
        }
    }

    // todo remove selectedGroup
    LaunchedEffect(selected) {
        push.reconnect.collectLatest {
            reload()
        }
    }

    LaunchedEffect(me) {
        reload()
        isLoading = false
    }

    // todo remove selectedGroup
    LaunchedEffect(selected) {
        groupUpdates.collectLatest {
            reload()
        }
    }

    NavTopBar(me, "Groups", onProfileClick = onProfileClick) {
        IconButton("search", "Search", styles = {
        }) {
            showSearch = !showSearch
        }
        IconButton("add", "New group", styles = {
            marginRight(.5.r)
        }) {
            scope.launch {
                val result = inputDialog(
                    "New group",
                    "Name",
                    "Create"
                )

                if (result == null) return@launch

                api.createGroup(emptyList()) { group ->
                    api.updateGroup(group.id!!, Group(name = result))
                    reload()
                    api.group(group.id!!) {
                        onSelected(GroupNav.Selected(it))
                    }
                }
            }
        }
    }

    if (showSearch) {
        NavSearchInput(searchText, { searchText = it }, onDismissRequest = {
            searchText = ""
            showSearch = false
        })
    }

    if (isLoading) {
        Loading()
    } else {
        Div({
            style {
                overflowY("auto")
                overflowX("hidden")
                padding(1.r / 2)
            }
        }) {
            if (!showSearch) {
                NavMenuItem("group", "Friends", selected = selected is GroupNav.Friends) {
                    onSelected(GroupNav.Friends)
                }
                NavMenuItem("location_on", "Local", selected = selected is GroupNav.Local) {
                    onSelected(GroupNav.Local)
                }
                Spacer()
            }

            if (shownGroups.isEmpty()) {
                Div({
                    style {
                        display(DisplayStyle.Flex)
                        alignItems(AlignItems.Center)
                        justifyContent(JustifyContent.Center)
                        opacity(.5)
                        padding(1.r)
                    }
                }) {
                    appText { noGroups }
                }
            } else {
                shownGroups.forEach { group ->
                    val myMember = group.members?.find { it.person?.id == me!!.id }
                    Div({
                        classes(
                            listOf(AppStyles.groupItem) + if ((selected as? GroupNav.Selected)?.group?.group?.id == group.group?.id) {
                                listOf(AppStyles.groupItemSelected)
                            } else {
                                emptyList()
                            }
                        )
                        onClick {
                            onSelected(GroupNav.Selected(group))
                        }

                        focusable()
                    }) {
                        GroupPhoto(group, me!!)
                        Div({
                            style {
                                width(0.px)
                                flexGrow(1)
                            }
                        }) {
                            Div({
                                classes(AppStyles.groupItemName)

                                style {
                                    if (group.isUnread(myMember?.member)) {
                                        fontWeight("bold")
                                    }
                                }
                            }) {
                                Text(group.name("Someone", "New group", listOf(me!!.id!!)))
                            }
                            Div({
                                classes(AppStyles.groupItemMessage)
                            }) {
                                if (group.latestMessage?.member == myMember?.member?.id) {
                                    Text("You: ")
                                }
                                Text(
                                    group.latestMessage?.preview() ?: "Created ${
                                        formatDistanceToNow(
                                            Date(group.group!!.createdAt!!),
                                            js("{ addSuffix: true }")
                                        )
                                    }"
                                )

                            }
                        }
                        if (group.latestMessage != null) {
                            Div({
                                style {
                                    marginLeft(.5.r)
                                    flexShrink(0)
                                }
                            }) {
                                Span({
                                    style {
                                        if (group.isUnread(myMember?.member)) {
                                            color(Styles.colors.primary)
                                            fontWeight("bold")
                                        } else {
                                            color(Styles.colors.secondary)
                                            opacity(.5)
                                        }
                                        fontSize(14.px)
                                    }
                                }) {
                                    Text(
                                        " ${
                                            group.group?.seen?.let {
                                                formatDistanceToNow(
                                                    Date(it),
                                                    js("{ addSuffix: true }")
                                                )
                                            }
                                        }"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun GroupExtended.name(someone: String, emptyGroup: String, omit: List<String>) =
    group?.name?.notBlank
        ?: members
            ?.filter { !omit.contains(it.person?.id) }
            ?.mapNotNull { it.person }
            ?.joinToString { it.name ?: someone }
            ?.notBlank
        ?: emptyGroup

fun GroupExtended.isUnread(member: Member?): Boolean {
    return (member?.seen?.let { Date(it) }?.getTime() ?: return false) < (latestMessage?.createdAt?.let { Date(it) }
        ?.getTime() ?: return false)
}
