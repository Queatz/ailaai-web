package app.reminder

import Reminder
import androidx.compose.runtime.*
import api
import apis.reminderOccurrences
import app.page.ReminderEvent
import app.page.SchedulePageStyles
import app.page.ScheduleView
import notBlank
import org.jetbrains.compose.web.css.margin
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text
import r
import kotlin.js.Date

@Composable
fun ReminderEvents(reminder: Reminder) {
    var events by remember(reminder) {
        mutableStateOf(emptyList<ReminderEvent>())
    }

    suspend fun reload() {
        api.reminderOccurrences(
            reminder.id!!,
            start = Date(reminder.start!!),
            end = reminder.end?.let(::Date) ?: Date()
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
                        event.date,
                        event.event,
                        event.occurrence?.done ?: false,
                        event.reminder.title?.notBlank ?: "New reminder",
                        event.occurrence?.note?.notBlank ?: event.reminder.note?.notBlank ?: "",
                        onDone = {
//                    onDone(event, it)
                        },
                        onEdit = {
//                    onEdit(event)
                        },
                        onDelete = {
//                    onDelete(event)
                        },
                        onRescheduleReminder = {
//                    onReschedule(event)
                        },
                        onOpenReminder = {
//                    onOpen(event)
                        }
                    )
                }
            }
        }
    }
}
