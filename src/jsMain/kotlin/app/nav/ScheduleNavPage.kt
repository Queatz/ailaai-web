package app.nav

import PaddingDefault
import androidx.compose.runtime.*
import app.AppStyles
import app.page.ScheduleView
import application
import components.IconButton
import focusable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import lib.*
import org.jetbrains.compose.web.attributes.autoFocus
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.attributes.selected
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLTextAreaElement
import kotlin.js.Date

@Composable
fun ScheduleNavPage(view: ScheduleView, onViewClick: (ScheduleView) -> Unit, onProfileClick: () -> Unit) {
    val me by application.me.collectAsState()
    val scope = rememberCoroutineScope()

    var note by remember {
        mutableStateOf("")
    }

    NavTopBar(me, "Reminders", onProfileClick) {
        IconButton("search", "Search", styles = {
            marginRight(.5.cssRem)
        }) {

        }
    }

    Div({
        style {
            overflowY("auto")
            overflowX("hidden")
            padding(PaddingDefault / 2)
            display(DisplayStyle.Flex)
            flexDirection(FlexDirection.Column)
        }
    }) {
        TextArea(note) {
            classes(Styles.textarea)
            style {
                margin(0.cssRem, .5.cssRem, .5.cssRem, .5.cssRem)
                height(3.5.cssRem)
                maxHeight(6.5.cssRem)
            }

            onKeyDown {
                if (it.key == "Enter" && !it.shiftKey) {
                    it.preventDefault()
                    it.stopPropagation()
                    note = ""
                    scope.launch {
                        delay(1)
                        (it.target as HTMLTextAreaElement).style.height = "0"
                        (it.target as HTMLTextAreaElement).style.height = "${(it.target as HTMLTextAreaElement).scrollHeight + 2}px"
                    }
                }
            }

            onInput {
                note = it.value
                it.target.style.height = "0"
                it.target.style.height = "${it.target.scrollHeight + 2}px"
            }

            onChange {
                it.target.style.height = "0"
                it.target.style.height = "${it.target.scrollHeight + 2}px"
            }

            placeholder("New reminder")

            autoFocus()

            ref { element ->
                element.focus()
                onDispose {}
            }
        }

        var until by remember(note == "") { mutableStateOf(false) }
        var reoccurs by remember(note == "") { mutableStateOf(false) }
        var date by remember(note == "") { mutableStateOf(format(Date(), "yyyy-MM-dd")) }
        var time by remember(note == "") { mutableStateOf(format(Date(), "HH:mm")) }

        if (note.isNotBlank()) {
            Div({
                style {
                    padding(.5.cssRem, .5.cssRem, 1.cssRem, .5.cssRem)
                    display(DisplayStyle.Flex)
                }
            }) {
                DateInput(date) {
                    classes(Styles.dateTimeInput)

                    style {
                        marginRight(1.cssRem)
                        padding(1.cssRem)
                        flex(1)
                    }

                    onChange {
                        date = it.value
                    }
                }

                TimeInput(time) {
                    classes(Styles.dateTimeInput)

                    style {
                        padding(1.cssRem)
                    }

                    onChange {
                        time = it.value
                    }
                }
            }
            Label(attrs = {
                style {
                    padding(0.cssRem, .5.cssRem, 1.cssRem, .5.cssRem)
                }
            }) {
                CheckboxInput(reoccurs) {
                    onChange {
                        reoccurs = it.value
                    }
                }
                Text("Reoccurs")
            }
            if (reoccurs) {
                Div({
                    style {
                        padding(0.cssRem, .5.cssRem)
                        display(DisplayStyle.Flex)
                        flexDirection(FlexDirection.Column)
                    }
                }) {
                    Select({
                        classes(Styles.dateTimeInput)
                    }, multiple = true) {
                        val t = setMinutes(startOfDay(Date()), getMinutes(parse(time, "HH:mm", Date())))
                        (0..23).forEach {
                            Option("$it") { Text(format(addHours(t, it.toDouble()), "h:mm a")) }
                        }
                    }

                    Select({
                        classes(Styles.dateTimeInput)
                        style {
                            marginTop(1.cssRem)
                        }
                    }, multiple = true) {
                        Option("Every day", { selected() }) { Text("Every day") }
                        (0..6).forEach {
                            val n = enUS.localize.day(it).toString()
                            Option("$it") { Text("$n") }
                        }
                        (1..31).forEach {
                            val n = enUS.localize.ordinalNumber(it).toString()
                            Option("$it") { Text("$n of the month") }
                        }
                        Option("the last day") { Text("Last day of the month") }
                    }

                    Select({
                        classes(Styles.dateTimeInput)
                        style {
                            marginTop(1.cssRem)
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
                            marginTop(1.cssRem)
                        }
                    }, multiple = true) {
                        Option("Every month", { selected() }) { Text("Every month") }
                        (0..11).forEach {
                            val n = enUS.localize.month(it).toString()
                            Option(n) { Text(n) }
                        }
                    }

                    Label(attrs = {
                        style {
                            padding(1.cssRem, .5.cssRem, 1.cssRem, .5.cssRem)
                        }
                    }) {
                        CheckboxInput(until) {
                            onChange {
                                until = it.value
                            }
                        }
                        Text("Until")
                    }

                    if (until) {
                        Div({
                            style {
                                display(DisplayStyle.Flex)
                                marginBottom(1.cssRem)
                            }
                        }) {
                            DateInput(date) {
                                classes(Styles.dateTimeInput)

                                style {
                                    marginRight(1.cssRem)
                                    padding(1.cssRem)
                                    flex(1)
                                }

                                onChange {
                                    date = it.value
                                }
                            }

                            TimeInput(time) {
                                classes(Styles.dateTimeInput)

                                style {
                                    padding(1.cssRem)
                                }

                                onChange {
                                    time = it.value
                                }
                            }
                        }
                    }
                }
            }

            Button({
                classes(Styles.button)

                style {
                    padding(.5.cssRem, 1.cssRem)
                    margin(0.cssRem, .5.cssRem, 1.cssRem, .5.cssRem)
                    justifyContent(JustifyContent.Center)
                    flexShrink(0)
                    fontWeight("bold")
                }

                onClick {
                    note = ""
                }
            }) {
                Text("Add reminder")
            }
        }

        // todo this is same as groupsnavpage Should be NavMainContent
        Div({
        }) {
            NavMenuItem("routine", "Daily", selected = view == ScheduleView.Daily) { onViewClick(ScheduleView.Daily) }
            NavMenuItem("calendar_view_week", "Weekly", selected = view == ScheduleView.Weekly) { onViewClick(ScheduleView.Weekly) }
            NavMenuItem("calendar_month", "Monthly", selected = view == ScheduleView.Monthly) { onViewClick(ScheduleView.Monthly) }
            NavMenuItem("auto_mode", "Yearly", selected = view == ScheduleView.Yearly) { onViewClick(ScheduleView.Yearly) }
        }
        Div({ style { height(1.cssRem) } })
        ReminderItem("Pet Mochi", selected = false) {}
    }
}

@Composable
fun ReminderItem(reminder: String, selected: Boolean, onSelected: () -> Unit) {
    Div({
        classes(
            listOf(AppStyles.groupItem) + if (selected) {
                listOf(AppStyles.groupItemSelected)
            } else {
                emptyList()
            }
        )
        focusable()
        onClick {
            onSelected()
        }
    }) {
        Div({
            style {
                width(0.px)
                flexGrow(1)
            }
        }) {
            Div({
                classes(AppStyles.groupItemName)
            }) {
                Text(reminder)
            }
            Div({
                classes(AppStyles.groupItemMessage)
            }) {
                Text("⏰ Every Sunday at 1pm and 9pm until January 1st, 2024")
            }
        }
    }
}
