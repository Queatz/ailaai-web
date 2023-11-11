import androidx.compose.runtime.*
import app.AppStyles
import app.ailaai.api.group
import app.ailaai.api.messages
import app.ailaai.api.messagesBefore
import app.components.LoadMore
import app.components.LoadMoreState
import app.group.GroupMessageBar
import app.group.GroupTopBar
import app.group.JoinGroupLayout
import app.messaages.MessageItem
import com.queatz.db.*
import components.Loading
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.js.Date

@Composable
fun GroupLayout(
    group: GroupExtended,
    onGroupUpdated: () -> Unit,
    onGroupGone: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val me by application.me.collectAsState()
    val myMember = group.members?.find { it.person?.id == me!!.id }
    val state = remember {
        LoadMoreState()
    }

    LaunchedEffect(group.group?.id) {
        group.group?.id?.let { groupId ->
            // Mark group as read
            api.group(groupId) {}
        }
    }

    var isLoading by remember(group.group?.id) {
        mutableStateOf(true)
    }

    var messages by remember(group.group?.id) {
        mutableStateOf(emptyList<Message>())
    }

    var hasMore by remember(group.group?.id) {
        mutableStateOf(true)
    }

    suspend fun reloadMessages() {
        api.messages(group.group!!.id!!) {
            messages = it
        }
        isLoading = false
    }

    suspend fun loadMore() {
        if (!hasMore || messages.isEmpty()) {
            return
        }
        api.messagesBefore(
            group.group!!.id!!,
            before = messages.lastOrNull()?.createdAt ?: return
        ) {
            val messagesCount = messages.size
            messages = (messages + it).distinctBy { it.id }

            if (messages.size == messagesCount) {
                hasMore = false
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

    LaunchedEffect(messages.firstOrNull()?.id) {
        state.scrollToBottom()
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
        LoadMore(
            state,
            hasMore && messages.isNotEmpty(),
            attrs = {
                classes(AppStyles.messages)
            },
            onLoadMore = {
                scope.launch {
                    loadMore()
                }
            }
        ) {
            messages.forEachIndexed { index, it ->
                MessageItem(
                    it,
                    if (index < messages.lastIndex) messages[index + 1] else null,
                    group.members?.find { member -> member.member?.id == it.member },
                    myMember
                )
            }
        }
    }

    GroupTopBar(
        group,
        onGroupUpdated = onGroupUpdated,
        onGroupGone = onGroupGone
    )
}
