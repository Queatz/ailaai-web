package app.messaages

import MemberAndPerson
import Message
import androidx.compose.runtime.Composable
import app.AppStyles
import app.softwork.routingcompose.Router
import components.ProfilePhoto
import kotlinx.browser.window
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.marginRight
import org.jetbrains.compose.web.dom.Div

@Composable
fun MessageItem(message: Message, member: MemberAndPerson?, myMember: MemberAndPerson?) {
    val isMe = message.member == myMember?.member?.id
    val router = Router.current

    Div({
        classes(
            listOf(AppStyles.messageLayout) + if (isMe) {
                listOf(AppStyles.myMessageLayout)
            } else {
                emptyList()
            }
        )
    }) {
        if (!isMe && member?.person != null) {
            ProfilePhoto(member.person!!, onClick = {
                window.open("/profile/${member.person!!.id!!}")
            }) {
                marginRight(.5.cssRem)
            }
        }
        MessageContent(message, myMember)
    }
}
