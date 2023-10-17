package app.group

import Group
import GroupExtended
import Member
import androidx.compose.runtime.*
import api
import app.AppStyles
import app.PageTopBar
import app.menu.Menu
import app.nav.name
import application
import components.ProfilePhoto
import dialog
import focusable
import inputDialog
import kotlinx.browser.window
import kotlinx.coroutines.launch
import lib.formatDistanceToNow
import notBlank
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.DOMRect
import org.w3c.dom.HTMLElement
import r
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
                "Group name",
                "",
                "Rename",
                defaultValue = group.group?.name ?: ""
            )

            if (name == null) return@launch

            api.updateGroup(group.group!!.id!!, Group(name = name)) {
                onGroupUpdated()
            }
        }
    }

    fun updateIntroduction() {
        scope.launch {
            val introduction = inputDialog(
                "Introduction",
                "",
                "Update",
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
            item("Members") {
                scope.launch {
                    dialog("Members (${group.members?.size ?: 0})", cancelButton = null) {
                        Div({
                            style {
                                display(DisplayStyle.Flex)
                                flexDirection(FlexDirection.Column)
                            }
                        }) {
                            group.members?.sortedByDescending { it.person?.seen?.let { Date(it).getTime() } ?: 0.0 }
                                ?.forEach { member ->
                                    Div({
                                        classes(
                                            listOf(AppStyles.groupItem, AppStyles.groupItemOnSurface)
                                        )
                                        onClick {
                                            window.open("/profile/${member.person!!.id}", "_blank")
                                        }

                                        focusable()
                                    }) {
                                        ProfilePhoto(member.person!!)
                                        Div({
                                            style {
                                                marginLeft(1.r)
                                            }
                                        }) {
                                            Div({
                                                classes(AppStyles.groupItemName)
                                            }) {
                                                Text(member.person?.name ?: "Someone")
                                            }
                                            Div({
                                                classes(AppStyles.groupItemMessage)
                                            }) {
                                                Text(
                                                    "Active ${
                                                        formatDistanceToNow(
                                                            Date(member.person!!.seen ?: member.person!!.createdAt!!),
                                                            js("{ addSuffix: true }")
                                                        )
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
            item("Rename") {
                renameGroup()
            }
            item("Introduction") {
                updateIntroduction()
            }
            item("Hide") {
                scope.launch {
                    api.updateMember(
                        myMember!!.member!!.id!!,
                        Member(hide = true)
                    ) {
                        onGroupGone()
                    }
                }
            }
            item("Leave") {
                scope.launch {
                    val result = dialog("Leave this group?", "Leave")

                    if (result == true) {
                        api.removeMember(
                            myMember!!.member!!.id!!
                        ) {
                            onGroupGone()
                        }
                    }
                }
            }
        }
    }

    if (showDescription) {
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

    PageTopBar(
        group.name("Someone", "New group", listOf(me!!.id!!)),
        group.members?.filter { it != myMember }?.maxByOrNull {
            it.person?.seen?.let { Date(it).getTime() } ?: 0.0
        }?.person?.seen?.let { Date(it) }?.let {
            "Active ${formatDistanceToNow(it, js("{ addSuffix: true }"))}"
        }
    ) {
        menuTarget = if (menuTarget == null) (it.target as HTMLElement).getBoundingClientRect() else null
    }
}
