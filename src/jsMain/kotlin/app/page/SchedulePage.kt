package app.page

import Reminder
import androidx.compose.runtime.*
import app.FullPageLayout
import app.reminder.EventRow
import app.reminder.ReminderPage
import lib.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text
import r
import kotlin.js.Date

enum class ScheduleView {
    Daily,
    Weekly,
    Monthly,
    Yearly
}

@Composable
fun SchedulePage(
    view: ScheduleView,
    reminder: Reminder?,
    onReminder: (Reminder?) -> Unit,
    onDelete: (Reminder) -> Unit
) {
    Style(SchedulePageStyles)

    if (reminder != null) {
        ReminderPage(
            reminder,
            onDelete = { onDelete(it) }
        )
        return
    }

    FullPageLayout {
        Div({
            style {
                display(DisplayStyle.Flex)
                flexDirection(FlexDirection.Column)
                padding(1.5.r, 1.r, 0.r, 1.r)
            }
        }) {
            var today = Date()

            if (view == ScheduleView.Weekly) {
                today = previousSunday(Date())
            }

            (0 until 12).forEach { monthIndex ->
                Div({
                    classes(SchedulePageStyles.title)
                }) {
                    when (view) {
                        ScheduleView.Daily -> {
                            Text((if (isToday(today)) "Today, " else if (isTomorrow(today)) "Tomorrow, " else "") + format(today, "EEEE, MMMM do"))
                            today = addDays(today, 1.0)
                        }
                        ScheduleView.Weekly -> {
                            Text(format(today, "EEEE, MMMM do"))
                            today = addWeeks(today, 1.0)
                        }
                        ScheduleView.Monthly -> {
                            Text(format(today, "MMMM, yyyy"))
                            today = addMonths(today, 1.0)
                        }
                        ScheduleView.Yearly -> {
                            Text(format(today, "yyyy G"))
                            today = addYears(today, 1.0)
                        }
                    }
                }
                Div({
                    classes(SchedulePageStyles.section)
                }) {
                    EventRow(view, "Pet Mochi")
                    EventRow(view, "Pet Lucy")
                    EventRow(view, "Pet Bingsu")
                    EventRow(view, "Pet Julia")
                }
            }
        }
    }
}

