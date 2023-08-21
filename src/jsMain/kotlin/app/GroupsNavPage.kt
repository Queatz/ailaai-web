package app

import GroupExtended
import Member
import PaddingDefault
import Styles
import androidx.compose.runtime.*
import app.messaages.preview
import application
import baseUrl
import components.GroupPhoto
import components.Loading
import http
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.utils.io.charsets.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import lib.formatDistanceToNow
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import push
import kotlin.js.Date

@Composable
fun GroupsNavPage(selectedGroup: GroupExtended?, onGroupSelected: (GroupExtended) -> Unit) {
    val me by application.me.collectAsState()
    var isLoading by remember {
        mutableStateOf(true)
    }
    var groups by remember {
        mutableStateOf(emptyList<GroupExtended>())
    }

    suspend fun reload() {
        application.bearerToken.first { it != null }
        try {
            groups = http.get("$baseUrl/groups") {
                contentType(ContentType.Application.Json.withCharset(Charsets.UTF_8))
                bearerAuth(application.bearer)
            }.body()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    LaunchedEffect(Unit) {
        push.events.collectLatest {
            reload()
        }
    }

    LaunchedEffect(me) {
        reload()
        isLoading = false
    }

    if (isLoading) {
        Loading()
    } else if (groups.isEmpty()) {
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
            groups.forEach { group ->
                val myMember = group.members?.find { it.person?.id == me!!.id }
                Div({
                    classes(AppStyles.groupItem)
                    onClick {
                        onGroupSelected(group)
                    }
                    style {
                        if (selectedGroup?.group?.id == group.group?.id) {
                            backgroundColor(Styles.colors.background)
                        }
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
                            Text(group.latestMessage?.preview() ?: "")
                        }
                    }
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
                            Text(" ${group.group?.seen?.let { formatDistanceToNow(Date(it), js("{ addSuffix: true }")) }}")
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
    return (member?.seen?.let { Date(it) }?.getTime() ?: return false) < (latestMessage?.createdAt?.let { Date(it) }?.getTime() ?: return false)
}
