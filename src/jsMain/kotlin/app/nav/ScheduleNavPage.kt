package app.nav

import PaddingDefault
import androidx.compose.runtime.*
import application
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import lib.format
import org.jetbrains.compose.web.attributes.autoFocus
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.attributes.selected
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLTextAreaElement
import kotlin.js.Date

@Composable
fun ScheduleNavPage() {
    val me by application.me.collectAsState()
    val scope = rememberCoroutineScope()

    var note by remember {
        mutableStateOf("")
    }

    NavTopBar(me, "Reminders")

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
                        Option("1:00pm", { selected() }) { Text("1:00pm") }
                        Option("2:00pm") { Text("2:00pm") }
                        Option("3:00pm") { Text("3:00pm") }
                        Option("4:00pm") { Text("4:00pm") }
                        Option("5:00pm") { Text("5:00pm") }
                        Option("6:00pm") { Text("6:00pm") }
                        Option("7:00pm") { Text("7:00pm") }
                        Option("8:00pm") { Text("8:00pm") }
                        Option("9:00pm") { Text("9:00pm") }
                        Option("10:00pm") { Text("10:00pm") }
                    }

                    Select({
                        classes(Styles.dateTimeInput)
                        style {
                            marginTop(1.cssRem)
                        }
                    }, multiple = true) {
                        Option("Every day", { selected() }) { Text("Every day") }
                        Option("Every Monday") { Text("Monday") }
                        Option("Tuesday") { Text("Tuesday") }
                        Option("Wednesday") { Text("Wednesday") }
                        Option("Thursday") { Text("Thursday") }
                        Option("Friday") { Text("Friday") }
                        Option("Saturday") { Text("Saturday") }
                        Option("Sunday") { Text("Sunday") }
                        Option("the 1st") { Text("1st of the month") }
                        Option("the last day") { Text("Last day of the month") }
                    }

                    Select({
                        classes(Styles.dateTimeInput)
                        style {
                            marginTop(1.cssRem)
                        }
                    }, multiple = true) {
                        Option("Every week", { selected() }) { Text("Every week") }
                        Option("Week 1") { Text("Week 1") }
                        Option("Week 2") { Text("Week 2") }
                        Option("Week 3") { Text("Week 3") }
                        Option("Week 4") { Text("Week 4") }
                        Option("Week 5") { Text("Week 5") }
                    }

                    Select({
                        classes(Styles.dateTimeInput)
                        style {
                            marginTop(1.cssRem)
                        }
                    }, multiple = true) {
                        Option("Every month", { selected() }) { Text("Every month") }
                        Option("January") { Text("January") }
                        Option("February") { Text("February") }
                        Option("March") { Text("March") }
                        Option("April") { Text("April") }
                        Option("May") { Text("May") }
                        Option("June") { Text("June") }
                        Option("July") { Text("July") }
                        Option("August") { Text("August") }
                        Option("September") { Text("September") }
                        Option("October") { Text("October") }
                        Option("November") { Text("November") }
                        Option("December") { Text("December") }
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
            NavMenuItem("routine", "Daily") {}
            NavMenuItem("calendar_view_week", "Weekly") {}
            NavMenuItem("calendar_month", "Monthly") {}
            NavMenuItem("rotate_right", "Yearly") {}
        }
    }
}
