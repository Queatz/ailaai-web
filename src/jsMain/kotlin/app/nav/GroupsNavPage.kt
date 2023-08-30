package app.nav

import CreateGroupBody
import GroupExtended
import Member
import Group
import PaddingDefault
import Styles
import androidx.compose.runtime.*
import app.AppStyles
import app.messaages.preview
import application
import baseUrl
import components.GroupPhoto
import components.IconButton
import components.Loading
import http
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.utils.io.charsets.*
import kotlinx.browser.window
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import lib.formatDistanceToNow
import org.jetbrains.compose.web.attributes.autoFocus
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import push
import kotlin.js.Date

@Composable
fun GroupsNavPage(selectedGroup: GroupExtended?, onGroupSelected: (GroupExtended) -> Unit) {
    val scope = rememberCoroutineScope()
    val me by application.me.collectAsState()
    var isLoading by remember {
        mutableStateOf(true)
    }
    var showSearch by remember {
        mutableStateOf(false)
    }
    var searchText by remember {
        mutableStateOf("")
    }
    var groups by remember {
        mutableStateOf(emptyList<GroupExtended>())
    }

    LaunchedEffect(selectedGroup) {
        searchText = ""
    }

    val shownGroups = remember(groups, searchText) {
        val search = searchText.trim()
        if (searchText.isBlank()) {
            groups
        } else {
            groups.filter {
                (it.group?.name?.contains(search, true) ?: false) || (it.members?.any { it.person?.name?.contains(search, true) ?: false } ?: false)
            }
        }
    }

    suspend fun reload() {
        application.bearerToken.first { it != null }
        try {
            groups = http.get("$baseUrl/groups") {
                contentType(ContentType.Application.Json.withCharset(Charsets.UTF_8))
                bearerAuth(application.bearer)
            }.body()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    LaunchedEffect(Unit) {
        push.events.collectLatest {
            reload()
        }
    }

    LaunchedEffect(Unit) {
        push.reconnect.collectLatest {
            reload()
        }
    }

    LaunchedEffect(me) {
        reload()
        isLoading = false
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
                try {
                    val group = http.post("$baseUrl/groups") {
                        setBody(CreateGroupBody(people = emptyList()))
                        contentType(ContentType.Application.Json.withCharset(Charsets.UTF_8))
                        bearerAuth(application.bearer)
                    }.body<Group>()
                    http.post("$baseUrl/groups/${group.id!!}") {
                        setBody(Group(name = name))
                        contentType(ContentType.Application.Json.withCharset(Charsets.UTF_8))
                        bearerAuth(application.bearer)
                    }
                    reload()
                    onGroupSelected(
                        http.get("$baseUrl/groups/${group.id!!}") {
                            contentType(ContentType.Application.Json.withCharset(Charsets.UTF_8))
                            bearerAuth(application.bearer)
                        }.body<GroupExtended>()
                    )
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
        }
    }
    if (showSearch) {
        TextInput(searchText) {
            classes(Styles.textarea)
            style {
                margin(.5.cssRem, 1.cssRem, 0.cssRem, 1.cssRem)
                height(3.5.cssRem)
                maxHeight(6.5.cssRem)
            }

            onKeyDown {
                if (it.key == "Escape") {
                    it.preventDefault()
                    it.stopPropagation()
                    searchText = ""
                    showSearch = false
                }
            }

            onInput {
                searchText = it.value
            }

            placeholder("Search")

            autoFocus()
        }
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
                                group.latestMessage?.preview() ?: "Created ${formatDistanceToNow(
                                    Date(group.group!!.createdAt!!),
                                    js("{ addSuffix: true }")
                                )}"
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
                                    color(Styles.colors.secondary)
                                    fontSize(14.px)
                                    opacity(.5)
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
    group?.name?.takeIf { it.isNotBlank() }
        ?: members
            ?.filter { !omit.contains(it.person?.id) }
            ?.mapNotNull { it.person }
            ?.joinToString { it.name ?: someone }
            ?.takeIf { it.isNotBlank() }
        ?: emptyGroup

fun GroupExtended.isUnread(member: Member?): Boolean {
    return (member?.seen?.let { Date(it) }?.getTime() ?: return false) < (latestMessage?.createdAt?.let { Date(it) }
        ?.getTime() ?: return false)
}
