package app.page

import Reminder
import ReminderOccurrence
import androidx.compose.runtime.*
import api
import apis.occurrences
import app.FullPageLayout
import app.reminder.EventRow
import app.reminder.ReminderPage
import components.Loading
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

enum class ReminderEvent {
    Start,
    Occur,
    End
}

data class ReminderInstance(
    val reminder: Reminder,
    val date: Date,
    val event: ReminderEvent,
    val occurrence: ReminderOccurrence?
)

@Composable
fun SchedulePage(
    view: ScheduleView,
    reminder: Reminder?,
    onReminder: (Reminder?) -> Unit,
    onDelete: (Reminder) -> Unit
) {
    Style(SchedulePageStyles)

    var isLoading by remember(view) {
        mutableStateOf(true)
    }

    var events by remember(view) {
        mutableStateOf(emptyList<ReminderInstance>())
    }

    if (reminder != null) {
        ReminderPage(
            reminder,
            onDelete = { onDelete(it) }
        )
        return
    }

    val range = mapOf(
        ScheduleView.Daily to 7,
        ScheduleView.Weekly to 4,
        ScheduleView.Monthly to 6,
        ScheduleView.Yearly to 2
    )

    LaunchedEffect(view) {
        val start = when (view) {
            ScheduleView.Daily -> startOfDay(Date())
            ScheduleView.Weekly -> startOfDay(Date())
            ScheduleView.Monthly -> startOfMonth(Date())
            ScheduleView.Yearly -> startOfYear(Date())
        }

        api.occurrences(
            start = start,
            end = when (view) {
                ScheduleView.Daily -> addDays(start, 7.0)
                ScheduleView.Weekly -> addWeeks(start, 4.0)
                ScheduleView.Monthly -> addMonths(start, 6.0)
                ScheduleView.Yearly -> addYears(start, 2.0)
            }
        ) {
            /**
             * Rules are:
             *
             * If reminder has a schedule -> Show occurrences
             * Else: Show start and end (if defined)
             */
            events = buildList {
                it.forEach {
                    if (it.reminder.schedule == null) {
                        add(
                            ReminderInstance(
                                it.reminder,
                                Date(it.reminder.start!!),
                                if (it.reminder.end == null) ReminderEvent.Occur else ReminderEvent.Start,
                                null
                            )
                        )
                        if (it.reminder.end != null) {
                            add(
                                ReminderInstance(
                                    it.reminder,
                                    Date(it.reminder.end!!),
                                    ReminderEvent.End,
                                    null
                                )
                            )
                        }
                    }

                    it.occurrences.forEach { occurrence ->
                        add(
                            ReminderInstance(
                                it.reminder,
                                Date(occurrence.date!!),
                                ReminderEvent.Occur,
                                occurrence
                            )
                        )
                    }

                    it.dates.filter { date ->
                        it.occurrences.none { it.occurrence == date }
                    }.forEach { date ->
                        add(
                            ReminderInstance(
                                it.reminder,
                                Date(date),
                                ReminderEvent.Occur,
                                null
                            )
                        )
                    }
                }
            }.sortedBy { it.date.getTime() }
        }
        isLoading = false
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

            (0 until range[view]!!).forEach { index ->
                val start = when (view) {
                    ScheduleView.Daily -> startOfDay(today)
                    ScheduleView.Weekly -> startOfDay(today)
                    ScheduleView.Monthly -> startOfMonth(today)
                    ScheduleView.Yearly -> startOfYear(today)
                }

                val end = when (view) {
                    ScheduleView.Daily -> addDays(start, 1.0)
                    ScheduleView.Weekly -> addWeeks(start, 1.0)
                    ScheduleView.Monthly -> addMonths(start, 1.0)
                    ScheduleView.Yearly -> addYears(start, 1.0)
                }

                Period(
                    view,
                    start,
                    end,
                    if (isLoading) null else events.filter { event ->
                        (isAfter(event.date, start) || isEqual(event.date, start)) &&
                        isBefore(event.date, end)
                    }
                )

                when (view) {
                    ScheduleView.Daily -> {
                        today = addDays(today, 1.0)
                    }

                    ScheduleView.Weekly -> {
                        today = addWeeks(today, 1.0)
                    }

                    ScheduleView.Monthly -> {
                        today = addMonths(today, 1.0)
                    }

                    ScheduleView.Yearly -> {
                        today = addYears(today, 1.0)
                    }
                }
            }
        }
    }
}

@Composable
fun Period(view: ScheduleView, start: Date, end: Date, events: List<ReminderInstance>?) {
    Div({
        classes(SchedulePageStyles.title)
    }) {
        when (view) {
            ScheduleView.Daily -> {
                Text(
                    (if (isToday(start)) "Today, " else if (isTomorrow(start)) "Tomorrow, " else "") + format(
                        start,
                        "EEEE, MMMM do"
                    )
                )
            }

            ScheduleView.Weekly -> {
                Text(format(start, "EEEE, MMMM do"))
            }

            ScheduleView.Monthly -> {
                Text(format(start, "MMMM, yyyy"))
            }

            ScheduleView.Yearly -> {
                Text(format(start, "yyyy G"))
            }
        }
    }
    Div({
        classes(SchedulePageStyles.section)
    }) {
        if (events.isNullOrEmpty()) {
            Div({
                style {
                    padding(.5.r)
                    color(Styles.colors.secondary)
                }
            }) {
                Text(if (events == null) "Loading…" else "No reminders")
            }
        } else {
            events.forEach { event ->
                EventRow(
                    view,
                    event.date,
                    event.event,
                    event.reminder.title ?: "New reminder",
                    event.reminder.note ?: ""
                )
            }
        }
    }
}
