package app.reminder

import Reminder
import androidx.compose.runtime.*
import api
import apis.deleteReminder
import apis.reminders
import apis.updateReminder
import app.PageTopBar
import app.components.EditField
import app.menu.Menu
import dialog
import inputDialog
import kotlinx.coroutines.launch
import lib.getTimezoneOffset
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.DOMRect
import org.w3c.dom.HTMLElement
import r
import kotlin.js.Date

@Composable
fun ReminderPage(reminder: Reminder, onUpdate: (Reminder) -> Unit, onDelete: (Reminder) -> Unit) {
    val scope = rememberCoroutineScope()

    var menuTarget by remember(reminder) {
        mutableStateOf<DOMRect?>(null)
    }

    val schedule by remember(reminder) {
        mutableStateOf(
            EditSchedule(
                initialReoccurs = reminder.schedule != null,
                initialUntil = reminder.end != null,
                initialDate = Date(reminder.start!!),
                initialUntilDate = reminder.end?.let(::Date),
                initialReoccurringHours = reminder.schedule?.hours,
                initialReoccurringDays = reminder.schedule?.days,
                initialReoccurringWeekdays = reminder.schedule?.weekdays,
                initialReoccurringWeeks = reminder.schedule?.weeks,
                initialReoccurringMonths = reminder.schedule?.months,
            )
        )
    }

    menuTarget?.let { target ->
        Menu({ menuTarget = null }, target) {
            item("Rename") {
                scope.launch {
                    val title = inputDialog("Reminder", "Title", "Update", defaultValue = reminder.title ?: "")

                    if (title == null) return@launch

                    api.updateReminder(reminder.id!!, Reminder(title = title)) {
                        onUpdate(it)
                    }
                }
            }

            item("Rechedule") {
                scope.launch {
                    val result = dialog("Reschedule", "Update") {
                        EditReminderSchedule(schedule)
                    }

                    if (result == true) {
                        api.updateReminder(
                            reminder.id!!, Reminder(
                                start = schedule.start,
                                end = schedule.end,
                                schedule = schedule.reminderSchedule
                            )
                        ) {
                            onUpdate(it)
                        }
                    }
                }
            }

//            item("Groups") {
//
//            }

            item("Delete") {
                scope.launch {
                    val result = dialog("Delete this reminder?", confirmButton = "Yes, delete") {
                        Text("You cannot undo this.")
                    }

                    if (result != true) return@launch

                    api.deleteReminder(reminder.id!!) {
                        onDelete(reminder)
                    }
                }
            }
        }
    }

    Div({
        style {
            flex(1)
            display(DisplayStyle.Flex)
            flexDirection(FlexDirection.Column)
            overflowY("auto")
            overflowX("hidden")
        }
    }) {
        EditField(reminder.note ?: "", "Note", {
            margin(1.r)
        }) {
            var success = false
            api.updateReminder(reminder.id!!, Reminder(note = it)) {
                success = true
                onUpdate(it)
            }

            success
        }
        Div({
            style {
                padding(1.r)
            }
        }) {
            Text("Reminder history and future, see all occurrences")
        }
    }
    PageTopBar(
        reminder.title ?: "new reminder",
        reminder.scheduleText
    ) {
        menuTarget = if (menuTarget == null) (it.target as HTMLElement).getBoundingClientRect() else null
    }
}
