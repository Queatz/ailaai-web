package app.group

import androidx.compose.runtime.*
import api
import app.AppStyles
import app.PageTopBar
import app.ailaai.api.removeMember
import app.ailaai.api.updateGroup
import app.ailaai.api.updateMember
import app.menu.InlineMenu
import app.menu.Menu
import app.nav.name
import appString
import application
import com.queatz.db.*
import dialog
import inputDialog
import joins
import kotlinx.coroutines.launch
import lib.formatDistanceToNow
import notBlank
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.DOMRect
import org.w3c.dom.HTMLElement
import kotlin.js.Date

@Composable
fun GroupTopBar(group: GroupExtended, onGroupUpdated: () -> Unit, onGroupGone: () -> Unit) {
    val me by application.me.collectAsState()
    val myMember = group.members?.find { it.person?.id == me?.id }
    val scope = rememberCoroutineScope()

    var menuTarget by remember {
        mutableStateOf<DOMRect?>(null)
    }

    var showDescription by remember(group) {
        mutableStateOf(true)
    }

    fun renameGroup() {
        scope.launch {
            val name = inputDialog(
                application.appString { groupName },
                "",
                application.appString { rename },
                defaultValue = group.group?.name ?: ""
            )

            if (name == null) return@launch

            api.updateGroup(group.group!!.id!!, Group(name = name)) {
                onGroupUpdated()
            }
        }
    }

    fun makeOpen(open: Boolean) {
        scope.launch {
            val result = dialog(
                if (open) "Open group" else "Close group",
                if (open) "Make group open" else "Make group closed",
            ) {
                if (open) {
                    Text("Anyone will be able to discover this group, see all members, messages, and request to become a member.")
                } else {
                    Text("This group will only be accessible to members.")
                }
            }

            if (result != true) return@launch

            api.updateGroup(group.group!!.id!!, Group(open = open)) {
                onGroupUpdated()
            }
        }
    }

    fun updateIntroduction() {
        scope.launch {
            val introduction = inputDialog(
                application.appString { introduction },
                "",
                application.appString { update },
                defaultValue = group.group?.description ?: ""
            )

            if (introduction == null) return@launch

            api.updateGroup(group.group!!.id!!, Group(description = introduction)) {
                onGroupUpdated()
            }
        }
    }

    if (menuTarget != null) {
        Menu({ menuTarget = null }, menuTarget!!) {
//            item("Pin") {
//
//            }
            item(appString { members }) {
                scope.launch {
                    groupMembersDialog(group)
                }
            }
            if (myMember != null) {
                item(appString { rename }) {
                    renameGroup()
                }
                item(appString { introduction }) {
                    updateIntroduction()
                }
                if (myMember.member?.host == true) {
                    val closeStr = appString { close }
                    item(appString { manage }) {
                        scope.launch {
                            dialog(
                                null,
                                closeStr,
                                null
                            ) {
                                InlineMenu({
                                    it(true)
                                }) {
                                    if (group.group?.open == true) {
                                        item("Make group closed") {
                                            makeOpen(false)
                                        }
                                    } else {
                                        item("Make group open") {
                                            makeOpen(true)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                item(appString { hide }) {
                    scope.launch {
                        api.updateMember(
                            myMember.member!!.id!!,
                            Member(hide = true)
                        ) {
                            onGroupGone()
                        }
                    }
                }
                item(appString { leave }) {
                    scope.launch {
                        val result = dialog("Leave this group?", application.appString { leave })

                        if (result == true) {
                            api.removeMember(
                                myMember.member!!.id!!
                            ) {
                                onGroupGone()
                            }
                        }
                    }
                }
            }
        }
    }

    val allJoinRequests by joins.joins.collectAsState()
    var joinRequests by remember {
        mutableStateOf(emptyList<JoinRequestAndPerson>())
    }

    LaunchedEffect(allJoinRequests) {
        joinRequests = allJoinRequests.filter { it.joinRequest?.group == group.group?.id}
    }

    if (joinRequests.isNotEmpty()) {
        Div({
            style {
                display(DisplayStyle.Flex)
                flexDirection(FlexDirection.Column)
                overflowX("hidden")
                overflowY("auto")
                maxHeight(25.vh)
            }
        }) {
            joinRequests.forEach {
                GroupJoinRequest(it) {
                    onGroupUpdated()
                }
            }
        }
    } else if (showDescription) {
        group.group?.description?.notBlank?.let { description ->
            Div({
                classes(AppStyles.groupDescription)

                onClick {
                    showDescription = false
                }

                title("Click to hide")
            }) {
                Text(description)
            }
        }
    }

    val active = group.members?.filter { it != myMember }?.maxByOrNull {
        it.person?.seen?.toEpochMilliseconds() ?: 0
    }?.person?.seen?.let { Date(it.toEpochMilliseconds()) }?.let {
        "${appString { active }} ${formatDistanceToNow(it, js("{ addSuffix: true }"))}"
    }

    PageTopBar(
        group.name(appString { someone }, appString { newGroup }, listOf(me!!.id!!)),
        if (group.group?.open == true) {
            listOfNotNull(appString { openGroup }, active).joinToString(" • ")
        } else {
            active
        }
    ) {
        menuTarget = if (menuTarget == null) (it.target as HTMLElement).getBoundingClientRect() else null
    }
}
