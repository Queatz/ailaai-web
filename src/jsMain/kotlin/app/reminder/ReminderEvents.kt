package app.reminder

import androidx.compose.runtime.*
import api
import apis.reminderOccurrences
import app.page.ReminderEvent
import app.page.SchedulePageStyles
import app.page.ScheduleView
import com.queatz.db.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.css.margin
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text
import r
import kotlin.js.Date

@Composable
fun ReminderEvents(reminder: Reminder) {
    val scope = rememberCoroutineScope()

    var events by remember(reminder) {
        mutableStateOf(emptyList<ReminderEvent>())
    }

    suspend fun reload() {
        api.reminderOccurrences(
            reminder.id!!,
            start = Date(reminder.start!!.toEpochMilliseconds()),
            end = reminder.end?.let { Date(it.toEpochMilliseconds()) } ?: Date()
        ) {
            events = it.toEvents().asReversed()
        }
    }

    LaunchedEffect(reminder) {
        reload()
    }

    if (events.isNotEmpty()) {
        Div({
            style {
                margin(1.r)
            }
        }) {
            Div({
                classes(SchedulePageStyles.title)
            }) {
                Text("History")
            }
            Div({
                classes(SchedulePageStyles.section)
            }) {
                events.forEach { event ->
                    EventRow(
                        ScheduleView.Yearly,
                        event,
                        showOpen = false,
                        onUpdate = {
                             scope.launch {
                                 reload()
                             }
                        },
                        onOpenReminder = {
                        }
                    )
                }
            }
        }
    }
}
