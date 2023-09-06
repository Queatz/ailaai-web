package app.nav

import Reminder
import androidx.compose.runtime.*
import api
import apis.newReminder
import apis.reminders
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
import org.jetbrains.compose.web.attributes.selected
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.events.Event
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
        scope.launch {
            isSavingReminder = true

            api.newReminder(
                Reminder(
                    title = newReminderTitle
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

            var until by remember(newReminderTitle == "") { mutableStateOf(false) }
            var reoccurs by remember(newReminderTitle == "") { mutableStateOf(false) }
            var date by remember(newReminderTitle == "") { mutableStateOf(format(Date(), "yyyy-MM-dd")) }
            var time by remember(newReminderTitle == "") { mutableStateOf(format(Date(), "HH:mm")) }

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
                        Select({
                            classes(Styles.dateTimeInput)

                            if (isSavingReminder) {
                                disabled()
                            }
                        }, multiple = true) {
                            val t = setMinutes(startOfDay(Date()), getMinutes(parse(time, "HH:mm", Date())))
                            (0..23).forEach {
                                Option("$it") { Text(format(addHours(t, it.toDouble()), "h:mm a")) }
                            }
                        }

                        Select({
                            classes(Styles.dateTimeInput)
                            style {
                                marginTop(1.r)
                            }

                            if (isSavingReminder) {
                                disabled()
                            }
                        }, multiple = true) {
                            Option("Every day", { selected() }) { Text("Every day") }
                            (1..7).forEach {
                                val n = enUS.localize.day(it - 1).toString()
                                Option("weekday:$it") { Text("$n") }
                            }
                            (1..31).forEach {
                                val n = enUS.localize.ordinalNumber(it).toString()
                                Option("day:$it") { Text("$n of the month") }
                            }
                            Option("-1") { Text("Last day of the month") }
                        }

                        Select({
                            classes(Styles.dateTimeInput)
                            style {
                                marginTop(1.r)
                            }

                            if (isSavingReminder) {
                                disabled()
                            }
                        }, multiple = true) {
                            Option("Every week", { selected() }) { Text("Every week") }
                            (1..5).forEach {
                                val n = enUS.localize.ordinalNumber(it).toString()
                                Option("$it") { Text("$n week") }
                            }
                        }

                        Select({
                            classes(Styles.dateTimeInput)
                            style {
                                marginTop(1.r)
                            }

                            if (isSavingReminder) {
                                disabled()
                            }
                        }, multiple = true) {
                            Option("Every month", { selected() }) { Text("Every month") }
                            (1..12).forEach {
                                val n = enUS.localize.month(it - 1).toString()
                                Option(n) { Text(n) }
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
            Div({ style { height(1.r) } })
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
