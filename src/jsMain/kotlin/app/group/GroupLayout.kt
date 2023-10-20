import androidx.compose.runtime.*
import app.AppStyles
import app.group.GroupMessageBar
import app.group.GroupTopBar
import app.group.JoinGroupLayout
import app.messaages.MessageItem
import components.Loading
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.SMOOTH
import org.w3c.dom.ScrollBehavior
import org.w3c.dom.ScrollToOptions

@Composable
fun GroupLayout(
    group: GroupExtended,
    onGroupUpdated: () -> Unit,
    onGroupGone: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val me by application.me.collectAsState()
    val myMember = group.members?.find { it.person?.id == me!!.id }

    LaunchedEffect(group.group?.id) {
        group.group?.id?.let { groupId ->
            // Mark group as read
            api.group(groupId) {}
        }
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var messages by remember {
        mutableStateOf(emptyList<Message>())
    }

    var latestMessage by remember {
        mutableStateOf<String?>(null)
    }

    var messagesDiv by remember {
        mutableStateOf<HTMLDivElement?>(null)
    }

    suspend fun reloadMessages() {
        api.groupMessages(group.group!!.id!!) {
            messages = it
        }
        isLoading = false

        if (messages.lastOrNull()?.id != latestMessage) {
            latestMessage = messages.lastOrNull()?.id
            scope.launch {
                delay(100)
                messagesDiv?.scroll(
                    ScrollToOptions(
                        top = messagesDiv!!.scrollHeight.toDouble(),
                        behavior = ScrollBehavior.SMOOTH
                    )
                )
            }
        }
    }

    LaunchedEffect(group.group?.id) {
        isLoading = true
        reloadMessages()
    }

    LaunchedEffect(group.group?.id) {
        push.events.collectLatest {
            reloadMessages()
        }
    }

    LaunchedEffect(group.group?.id) {
        push.reconnect.collectLatest {
            reloadMessages()
        }
    }

    if (isLoading) {
        Loading()
    } else {
        if (myMember != null) {
            GroupMessageBar(group) {
                reloadMessages()
            }
        } else {
            JoinGroupLayout(group)
        }
        Div({
            classes(AppStyles.messages)
            ref {
                messagesDiv = it

                onDispose {
                    messagesDiv = null
                }
            }
        }) {
            messages.forEachIndexed { index, it ->
                MessageItem(
                    it,
                    if (index < messages.lastIndex - 1) messages[index + 1] else null,
                    group.members?.find { member -> member.member?.id == it.member },
                    myMember
                )
            }
        }

        GroupTopBar(
            group,
            onGroupUpdated = onGroupUpdated,
            onGroupGone = onGroupGone
        )
    }
}
