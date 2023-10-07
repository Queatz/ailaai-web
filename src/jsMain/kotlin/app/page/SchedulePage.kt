package app.page

import Reminder
import ReminderOccurrence
import androidx.compose.runtime.*
import api
import apis.deleteReminderOccurrence
import apis.occurrences
import apis.updateReminderOccurrence
import app.FullPageLayout
import app.reminder.EventRow
import app.reminder.ReminderDateTime
import app.reminder.ReminderPage
import app.reminder.toEvents
import components.IconButton
import dialog
import inputDialog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import lib.*
import notBlank
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text
import parseDateTime
import r
import kotlin.js.Date

enum class ScheduleView {
    Daily,
    Weekly,
    Monthly,
    Yearly
}

enum class ReminderEventType {
    Start,
    Occur,
    End
}

data class ReminderEvent(
    /**
     * The reminder that spawned this event
     */
    val reminder: Reminder,
    /**
     * The date of the event
     */
    val date: Date,
    /**
     * The type of event.
     */
    val event: ReminderEventType,
    /**
     * The `ReminderOccurrence` associated with this event, if any.
     */
    val occurrence: ReminderOccurrence?
)

@Composable
fun SchedulePage(
    view: ScheduleView,
    reminder: Reminder?,
    goToToday: Flow<Unit>,
    onReminder: (Reminder?) -> Unit,
    onUpdate: (Reminder) -> Unit,
    onDelete: (Reminder) -> Unit
) {
    Style(SchedulePageStyles)

    val scope = rememberCoroutineScope()

    val changes = remember {
        MutableSharedFlow<Unit>()
    }

    var isLoading by remember(view) {
        mutableStateOf(true)
    }

    var events by remember(view) {
        mutableStateOf(emptyList<ReminderEvent>())
    }

    var offset by remember {
        mutableStateOf(startOfDay(Date()))
    }

    LaunchedEffect(view) {
        offset = startOfDay(Date())
        goToToday.collectLatest {
            offset = startOfDay(Date())
        }
    }

    if (reminder != null) {
        ReminderPage(
            reminder,
            onUpdate = { onUpdate(it) },
            onDelete = { onDelete(it) }
        )
        return
    }

    fun move(amount: Double) {
        offset = when (view) {
            ScheduleView.Daily -> addDays(offset, amount)
            ScheduleView.Weekly -> addWeeks(offset, amount)
            ScheduleView.Monthly -> addMonths(offset, amount)
            ScheduleView.Yearly -> addYears(offset, amount)
        }
    }

    val range = mapOf(
        ScheduleView.Daily to 7,
        ScheduleView.Weekly to 4,
        ScheduleView.Monthly to 3,
        ScheduleView.Yearly to 2
    )

    suspend fun reload() {
        val start = when (view) {
            ScheduleView.Daily -> startOfDay(offset)
            ScheduleView.Weekly -> startOfWeek(offset)
            ScheduleView.Monthly -> startOfMonth(offset)
            ScheduleView.Yearly -> startOfYear(offset)
        }

        api.occurrences(
            start = start,
            end = when (view) {
                ScheduleView.Daily -> addDays(start, range[ScheduleView.Daily]!!.toDouble())
                ScheduleView.Weekly -> addWeeks(start, range[ScheduleView.Weekly]!!.toDouble())
                ScheduleView.Monthly -> addMonths(start, range[ScheduleView.Monthly]!!.toDouble())
                ScheduleView.Yearly -> addYears(start, range[ScheduleView.Yearly]!!.toDouble())
            }
        ) {
            events = it.toEvents()
        }
        isLoading = false
    }

    LaunchedEffect(view, offset) {
        reload()

        changes.collectLatest {
            reload()
        }
    }

    FullPageLayout {
        Div({
            style {
                display(DisplayStyle.Flex)
                flexDirection(FlexDirection.Column)
                padding(1.5.r, 1.r, 0.r, 1.r)
            }
        }) {
            var today = offset

            IconButton("keyboard_arrow_up", when (view) {
                ScheduleView.Daily -> "Previous day"
                ScheduleView.Weekly -> "Previous week"
                ScheduleView.Monthly -> "Previous month"
                ScheduleView.Yearly -> "Previous year"
            }) {
                move(-1.0)
            }

            (0 until range[view]!!).forEach { index ->
                val start = when (view) {
                    ScheduleView.Daily -> startOfDay(today)
                    ScheduleView.Weekly -> startOfWeek(today)
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
                        (isAfter(event.date, start) || isEqual(event.date, start)) && isBefore(event.date, end)
                    },
                    onUpdate = {
                        scope.launch {
                            changes.emit(Unit)
                            onUpdate(it.reminder)
                        }
                    },
                    onOpen = {
                        onReminder(it.reminder)
                    }
                )

                today = end
            }

            IconButton("keyboard_arrow_down", when (view) {
                ScheduleView.Daily -> "Next day"
                ScheduleView.Weekly -> "Next week"
                ScheduleView.Monthly -> "Next month"
                ScheduleView.Yearly -> "Next year"
            }) {
                move(1.0)
            }
        }
    }
}

@Composable
fun Period(
    view: ScheduleView,
    start: Date,
    end: Date,
    events: List<ReminderEvent>?,
    onUpdate: (ReminderEvent) -> Unit,
    onOpen: (ReminderEvent) -> Unit,
) {
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
                    event,
                    onUpdate = {
                        onUpdate(event)
                    },
                    onOpenReminder = {
                        onOpen(event)
                    }
                )
            }
        }
    }
}
