package app.nav

import Reminder
import ReminderSchedule
import androidx.compose.runtime.*
import api
import apis.newReminder
import apis.reminders
import app.components.MultiSelect
import app.components.Spacer
import app.page.ScheduleView
import app.reminder.ReminderItem
import application
import components.IconButton
import components.Loading
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import lib.*
import org.jetbrains.compose.web.attributes.autoFocus
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.events.Event
import parseDateTime
import r
import kotlin.js.Date

@Composable
fun ScheduleNavPage(
    reminderUpdates: Flow<Reminder>,
    reminder: Reminder?,
    onReminder: (Reminder) -> Unit,
    view: ScheduleView,
    onViewClick: (ScheduleView) -> Unit,
    onProfileClick: () -> Unit
) {
    val me by application.me.collectAsState()
    val scope = rememberCoroutineScope()

    var newReminderTitle by remember {
        mutableStateOf("")
    }

    var isSavingReminder by remember {
        mutableStateOf(false)
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var reminders by remember {
        mutableStateOf(emptyList<Reminder>())
    }

    var showSearch by remember {
        mutableStateOf(false)
    }

    var searchText by remember {
        mutableStateOf("")
    }

    LaunchedEffect(reminder, view) {
        searchText = ""
        showSearch = false
    }

    val shownReminders = remember(reminders, searchText) {
        val search = searchText.trim()
        if (searchText.isBlank()) {
            reminders
        } else {
            reminders.filter {
                it.title?.contains(search, true) ?: false
            }
        }
    }

    var onValueChange by remember { mutableStateOf({}) }

    var reoccurs by remember(newReminderTitle == "") { mutableStateOf(false) }
    var date by remember(newReminderTitle == "") { mutableStateOf(format(Date(), "yyyy-MM-dd")) }
    var time by remember(newReminderTitle == "") { mutableStateOf(format(Date(), "HH:mm")) }
    var until by remember(newReminderTitle == "") { mutableStateOf(false) }
    var untilDate by remember(newReminderTitle == "") { mutableStateOf(format(Date(), "yyyy-MM-dd")) }
    var untilTime by remember(newReminderTitle == "") { mutableStateOf(format(Date(), "HH:mm")) }
    var reoccurringHours by remember(newReminderTitle == "", reoccurs) { mutableStateOf(listOf(time.split(":").first().toInt().toString())) }
    var reoccurringDays by remember(newReminderTitle == "") { mutableStateOf(emptyList<String>()) }
    var reoccurringWeeks by remember(newReminderTitle == "") { mutableStateOf(emptyList<String>()) }
    var reoccurringMonths by remember(newReminderTitle == "") { mutableStateOf(emptyList<String>()) }

    val t = setMinutes(startOfDay(Date()), getMinutes(parse(time, "HH:mm", Date())))

    LaunchedEffect(reoccurringHours) {
        if (reoccurringHours.isEmpty()) reoccurringHours = listOf("${t.getHours()}")
    }
    LaunchedEffect(reoccurringDays) {
        if (reoccurringDays.isEmpty()) reoccurringDays = listOf("-") else if (reoccurringDays.size > 1 && "-" in reoccurringDays) reoccurringDays -= "-"
    }
    LaunchedEffect(reoccurringWeeks) {
        if (reoccurringWeeks.isEmpty()) reoccurringWeeks = listOf("-") else if (reoccurringWeeks.size > 1 && "-" in reoccurringWeeks) reoccurringWeeks -= "-"
    }
    LaunchedEffect(reoccurringMonths) {
        if (reoccurringMonths.isEmpty()) reoccurringMonths = listOf("-") else if (reoccurringMonths.size > 1 && "-" in reoccurringMonths) reoccurringMonths -= "-"
    }

    LaunchedEffect(newReminderTitle) {
        onValueChange()
    }

    fun reload() {
        scope.launch {
            api.reminders {
                reminders = it
            }
            isLoading = false
        }
    }

    fun addReminder() {
        val timezone = systemTimezone

        if (newReminderTitle.isBlank()) {
            return
        }

        scope.launch {
            isSavingReminder = true

            api.newReminder(
                Reminder(
                    title = newReminderTitle,
                    start = parseDateTime(date, time).toISOString(),
                    end = if (until) parseDateTime(untilDate, untilTime).toISOString() else null,
                    timezone = timezone,
                    utcOffset = getTimezoneOffset(timezone) / (60 * 60 * 1000),
                    schedule = if (reoccurs) {
                        ReminderSchedule(
                            hours = reoccurringHours.filter { it != "-" }.map { it.toInt() },
                            days = reoccurringDays.filter { it != "-" }.mapNotNull { it.split(":").let { if (it[0] == "day") it[1] else null } }.map { it.toInt() },
                            weekdays = reoccurringDays.filter { it != "-" }.mapNotNull { it.split(":").let { if (it[0] == "weekday") it[1] else null } }.map { it.toInt() },
                            weeks = reoccurringWeeks.filter { it != "-" }.map { it.toInt() },
                            months = reoccurringMonths.filter { it != "-" }.map { it.toInt() },
                        )
                    } else {
                        null
                    }
                )
            ) {
                newReminderTitle = ""
                reload()
            }

            isSavingReminder = false
        }
    }

    LaunchedEffect(Unit) {
        reload()
    }

    LaunchedEffect(reminder) {
        reminderUpdates.collectLatest {
            reload()
        }
    }

    NavTopBar(me, "Reminders", onProfileClick) {
        IconButton("search", "Search", styles = {
            marginRight(.5.r)
        }) {
            showSearch = !showSearch
        }
    }

    if (showSearch) {
        NavSearchInput(searchText, { searchText = it }, onDismissRequest = {
            searchText = ""
            showSearch = false
        })
    }

    Div({
        style {
            overflowY("auto")
            overflowX("hidden")
            padding(1.r / 2)
            display(DisplayStyle.Flex)
            flexDirection(FlexDirection.Column)
        }
    }) {
        if (!showSearch) {
            TextArea(newReminderTitle) {
                classes(Styles.textarea)
                style {
                    margin(0.r, .5.r, .5.r, .5.r)
                    height(3.5.r)
                    maxHeight(6.5.r)
                }

                onKeyDown {
                    if (it.key == "Enter" && !it.shiftKey) {
                        it.preventDefault()
                        it.stopPropagation()
                        addReminder()
                    }
                }

                onInput {
                    newReminderTitle = it.value
                    it.target.style.height = "0"
                    it.target.style.height = "${it.target.scrollHeight + 2}px"
                }

                onChange {
                    it.target.style.height = "0"
                    it.target.style.height = "${it.target.scrollHeight + 2}px"

                    if (newReminderTitle.isEmpty()) {
                        it.target.focus()
                    }
                }

                if (isSavingReminder) {
                    disabled()
                }

                placeholder("New reminder")

                autoFocus()

                ref { element ->
                    element.focus()

                    onValueChange = { element.dispatchEvent(Event("change")) }

                    onDispose {
                        onValueChange = {}
                    }
                }
            }

            if (newReminderTitle.isNotBlank()) {
                Div({
                    style {
                        padding(.5.r, .5.r, 1.r, .5.r)
                        display(DisplayStyle.Flex)
                    }
                }) {
                    DateInput(date) {
                        classes(Styles.dateTimeInput)

                        style {
                            marginRight(1.r)
                            padding(1.r)
                            flex(1)
                        }

                        onChange {
                            date = it.value
                        }

                        if (isSavingReminder) {
                            disabled()
                        }
                    }

                    TimeInput(time) {
                        classes(Styles.dateTimeInput)

                        style {
                            padding(1.r)
                        }

                        onChange {
                            time = it.value
                        }

                        if (isSavingReminder) {
                            disabled()
                        }
                    }
                }
                Label(attrs = {
                    style {
                        padding(0.r, .5.r, 1.r, .5.r)
                    }
                }) {
                    CheckboxInput(reoccurs) {
                        onChange {
                            reoccurs = it.value
                        }

                        if (isSavingReminder) {
                            disabled()
                        }
                    }
                    Text("Reoccurs")
                }
                if (reoccurs) {
                    Div({
                        style {
                            padding(0.r, .5.r)
                            marginBottom(1.r)
                            display(DisplayStyle.Flex)
                            flexDirection(FlexDirection.Column)
                        }
                    }) {
                        MultiSelect(reoccurringHours, { reoccurringHours = it }, {
                            if (isSavingReminder) {
                                disabled()
                            }
                        }) {
                            (0..23).forEach {
                                option("$it", format(addHours(t, it.toDouble()), "h:mm a"))
                            }
                        }

                        MultiSelect(reoccurringDays, { reoccurringDays = it }, {
                            style {
                                marginTop(1.r)
                            }

                            if (isSavingReminder) {
                                disabled()
                            }
                        }) {
                            option("-", "Every day")
                            (1..7).forEach {
                                val n = enUS.localize.day(it - 1).toString()
                                option("weekday:$it", n)
                            }
                            (1..31).forEach {
                                option("day:$it", "${enUS.localize.ordinalNumber(it)} of the month")
                            }
                            option("day:-1", "Last day of the month")
                        }

                        MultiSelect(reoccurringWeeks, { reoccurringWeeks = it }, {
                            style {
                                marginTop(1.r)
                            }

                            if (isSavingReminder) {
                                disabled()
                            }
                        }) {
                            option("-", "Every week")
                            (1..5).forEach {
                                option("$it", "${enUS.localize.ordinalNumber(it)} week")
                            }
                        }

                        MultiSelect(reoccurringMonths, { reoccurringMonths = it }, {
                            style {
                                marginTop(1.r)
                            }

                            if (isSavingReminder) {
                                disabled()
                            }
                        }) {
                            option("-", "Every month")
                            (1..12).forEach {
                                option("$it", enUS.localize.month(it - 1).toString())
                            }
                        }
                    }
                }

                Label(attrs = {
                    style {
                        padding(0.r, .5.r, 1.r, .5.r)
                    }
                }) {
                    CheckboxInput(until) {
                        onChange {
                            until = it.value
                        }

                        if (isSavingReminder) {
                            disabled()
                        }
                    }
                    Text("Until")
                }

                if (until) {
                    Div({
                        style {
                            display(DisplayStyle.Flex)
                            marginBottom(1.r)
                            padding(0.r, .5.r)
                        }
                    }) {
                        DateInput(untilDate) {
                            classes(Styles.dateTimeInput)

                            style {
                                marginRight(1.r)
                                padding(1.r)
                                flex(1)
                            }

                            onChange {
                                untilDate = it.value
                            }

                            if (isSavingReminder) {
                                disabled()
                            }
                        }

                        TimeInput(untilTime) {
                            classes(Styles.dateTimeInput)

                            style {
                                padding(1.r)
                            }

                            onChange {
                                untilTime = it.value
                            }

                            if (isSavingReminder) {
                                disabled()
                            }
                        }
                    }
                }

                Button({
                    classes(Styles.button)

                    style {
                        padding(.5.r, 1.r)
                        margin(0.r, .5.r, 1.r, .5.r)
                        justifyContent(JustifyContent.Center)
                        flexShrink(0)
                        fontWeight("bold")
                    }

                    onClick {
                        addReminder()
                    }

                    if (isSavingReminder) {
                        disabled()
                    }
                }) {
                    Text("Add reminder")
                }
            }

            if (newReminderTitle.isNotBlank()) {
                return@Div
            }

            // todo this is same as groupsnavpage Should be NavMainContent
            Div({
            }) {
                NavMenuItem(
                    "routine",
                    "Daily",
                    selected = reminder == null && view == ScheduleView.Daily
                )
                { onViewClick(ScheduleView.Daily) }
                NavMenuItem(
                    "calendar_view_week",
                    "Weekly",
                    selected = reminder == null && view == ScheduleView.Weekly
                ) { onViewClick(ScheduleView.Weekly) }
                NavMenuItem(
                    "calendar_month",
                    "Monthly",
                    selected = reminder == null && view == ScheduleView.Monthly
                ) { onViewClick(ScheduleView.Monthly) }
                NavMenuItem(
                    "auto_mode",
                    "Yearly",
                    selected = reminder == null && view == ScheduleView.Yearly
                ) { onViewClick(ScheduleView.Yearly) }
            }
            Spacer()
        }

        if (isLoading) {
            Loading()
        } else {
            shownReminders.forEach {
                ReminderItem(it, selected = reminder?.id == it.id) {
                    onReminder(it)
                }
            }
        }
    }
}
