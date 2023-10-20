package components

import GroupExtended
import Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import r
import kotlin.js.Date

@Composable
fun GroupPhoto(group: GroupExtended, me: Person) {
    val otherMembers = remember(group) {
        group.members?.filter { it.person?.id != me.id }?.sortedByDescending { it.member?.seen?.let { Date(it).getTime() } ?: 0.0 } ?: emptyList()
    }

    if (otherMembers.size > 1) {
        Div({
            style {
                width(54.px)
                height(54.px)
                position(Position.Relative)
                marginRight(.5.r)
            }
        }) {
            ProfilePhoto(otherMembers[1].person ?: me, size = 33.px, border = true) {
                position(Position.Absolute)
                top(0.r)
                right(0.r)
            }
            ProfilePhoto(otherMembers[0].person ?: me, size = 33.px, border = true) {
                position(Position.Absolute)
                bottom(0.r)
                left(0.r)
            }
        }
    } else {
        ProfilePhoto(otherMembers.firstOrNull()?.person ?: me, size = 54.px) {
            marginRight(.5.r)
        }
    }
}
