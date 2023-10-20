package app.group

import GroupExtended
import app.AppStyles
import components.ProfilePhoto
import dialog
import focusable
import kotlinx.browser.window
import lib.formatDistanceToNow
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text
import r
import kotlin.js.Date

suspend fun groupMembersDialog(group: GroupExtended) = dialog("Members (${group.members?.size ?: 0})", cancelButton = null, confirmButton = "Close") {
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
