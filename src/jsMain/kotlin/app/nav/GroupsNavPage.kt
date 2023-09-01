package app.nav

import Group
import GroupExtended
import Member
import PaddingDefault
import Styles
import androidx.compose.runtime.*
import api
import app.AppStyles
import app.messaages.preview
import application
import components.GroupPhoto
import components.IconButton
import components.Loading
import kotlinx.browser.window
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
import kotlin.js.Date

@Composable
fun GroupsNavPage(
    groupUpdates: Flow<Unit>,
    selectedGroup: GroupExtended?,
    onGroupSelected: (GroupExtended?) -> Unit
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

    LaunchedEffect(selectedGroup) {
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
        application.bearerToken.first { it != null }
        api.groups {
            groups = it
            onGroupSelected(groups.firstOrNull { it.group?.id == selectedGroup?.group?.id })
        }
    }

    // todo remove selectedGroup
    LaunchedEffect(selectedGroup) {
        push.events.collectLatest {
            reload()
        }
    }

    // todo remove selectedGroup
    LaunchedEffect(selectedGroup) {
        push.reconnect.collectLatest {
            reload()
        }
    }

    LaunchedEffect(me) {
        reload()
        isLoading = false
    }

    // todo remove selectedGroup
    LaunchedEffect(selectedGroup) {
        groupUpdates.collectLatest {
            reload()
        }
    }


    NavTopBar(me, "Groups") {
        IconButton("search", "Search", styles = {
            marginRight(1.cssRem)
        }) {
            showSearch = !showSearch
        }
        IconButton("add", "New group", styles = {
            marginRight(1.cssRem)
        }) {
            scope.launch {
                val name = window.prompt("Group name")
                if (name == null) return@launch
                api.createGroup(emptyList()) { group ->
                    api.updateGroup(group.id!!, Group(name = name))
                    reload()
                    api.group(group.id!!) {
                        onGroupSelected(it)
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
    } else if (shownGroups.isEmpty()) {
        Div({
            style {
                height(100.percent)
                display(DisplayStyle.Flex)
                alignItems(AlignItems.Center)
                justifyContent(JustifyContent.Center)
                opacity(.5)
            }
        }) {
            Text("No groups")
        }
    } else {
        Div({
            style {
                overflowY("auto")
                overflowX("hidden")
                padding(PaddingDefault / 2)
            }
        }) {
            shownGroups.forEach { group ->
                val myMember = group.members?.find { it.person?.id == me!!.id }
                Div({
                    classes(
                        listOf(AppStyles.groupItem) + if (selectedGroup?.group?.id == group.group?.id) {
                            listOf(AppStyles.groupItemSelected)
                        } else {
                            emptyList()
                        }
                    )
                    onClick {
                        onGroupSelected(group)
                    }
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
                                marginLeft(.5.cssRem)
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
