package components

import GroupExtended
import Person
import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div

@Composable
fun GroupPhoto(group: GroupExtended, me: Person) {
    val otherMembers = group.members?.filter { it.person?.id != me.id }?.shuffled() ?: emptyList()

    if (otherMembers.size > 1) {
        Div({
            style {
                width(54.px)
                height(54.px)
                position(Position.Relative)
                marginRight(.5.cssRem)
            }
        }) {
            ProfilePhoto(otherMembers[0].person ?: me, size = 33.px) {
                position(Position.Absolute)
                top(0.cssRem)
                right(0.cssRem)
                border(3.px, LineStyle.Solid, Color.white)
            }
            ProfilePhoto(otherMembers[1].person ?: me, size = 33.px) {
                position(Position.Absolute)
                bottom(0.cssRem)
                left(0.cssRem)
                border(3.px, LineStyle.Solid, Color.white)
            }
        }
    } else {
        ProfilePhoto(otherMembers.firstOrNull()?.person ?: me, size = 54.px) {
            marginRight(.5.cssRem)
        }
    }
}