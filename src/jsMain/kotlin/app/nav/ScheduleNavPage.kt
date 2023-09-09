package app.nav

import Reminder
import Styles
import androidx.compose.runtime.*
import api
import apis.newReminder
import apis.reminders
import app.components.Spacer
import app.page.ScheduleView
import app.reminder.*
import application
import components.IconButton
import components.Loading
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import lib.getTimezoneOffset
import lib.systemTimezone
import org.jetbrains.compose.web.attributes.autoFocus
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.dom.TextArea
import org.w3c.dom.events.Event
import r

@Composable
fun ScheduleNavPage(
    reminderUpdates: Flow<Reminder>,
    reminder: Reminder?,
    onReminder: (Reminder?) -> Unit,
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

    val schedule by remember(newReminderTitle == "") {
        mutableStateOf(EditSchedule())
    }

    LaunchedEffect(newReminderTitle) {
        onValueChange()
    }

    fun reload() {
        scope.launch {
            api.reminders {
                reminders = it

                if (reminder != null) {
                    onReminder(reminders.firstOrNull { it.id == reminder.id })
                }
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
                    start = schedule.start,
                    end = schedule.end,
                    timezone = timezone,
                    utcOffset = getTimezoneOffset(timezone) / (60 * 60 * 1000),
                    schedule = schedule.reminderSchedule
                )
            ) {
                newReminderTitle = ""
                onReminder(it)
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
            // todo can be EditField
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
                EditReminderSchedule(schedule)

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
