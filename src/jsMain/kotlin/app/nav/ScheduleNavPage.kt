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

        var until by remember { mutableStateOf(false) }
        var reoccurs by remember { mutableStateOf(false) }
        var date by remember { mutableStateOf(format(Date(), "yyyy-MM-dd")) }
        var time by remember { mutableStateOf(format(Date(), "HH:mm")) }

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
                        Option("Every day", { selected() }) { Text("Every day") }
                        Option("Every Monday") { Text("Every Monday") }
                        Option("Every Tuesday") { Text("Every Tuesday") }
                        Option("Every Wednesday") { Text("Every Wednesday") }
                        Option("Every Thursday") { Text("Every Thursday") }
                        Option("Every Friday") { Text("Every Friday") }
                        Option("Every Saturday") { Text("Every Saturday") }
                        Option("Every Sunday") { Text("Every Sunday") }
                        Option("Every 1st day") { Text("Every 1st day") }
                        Option("Every last day") { Text("Every last day") }
                    }

                    Select({
                        classes(Styles.dateTimeInput)
                        style {
                            marginTop(1.cssRem)
                        }
                    }, multiple = true) {
                        Option("Of every week", { selected() }) { Text("Of every week") }
                        Option("Of week 1") { Text("Of week 1") }
                        Option("Of week 2") { Text("Of week 2") }
                        Option("Of week 3") { Text("Of week 3") }
                        Option("Of week 4") { Text("Of week 4") }
                        Option("Of week 5") { Text("Of week 5") }
                    }

                    Select({
                        classes(Styles.dateTimeInput)
                        style {
                            marginTop(1.cssRem)
                        }
                    }, multiple = true) {
                        Option("Of every month", { selected() }) { Text("Of every month") }
                        Option("Of January") { Text("Of January") }
                        Option("Of February") { Text("Of February") }
                        Option("Of March") { Text("Of March") }
                        Option("Of April") { Text("Of April") }
                        Option("Of May") { Text("Of May") }
                        Option("Of June") { Text("Of June") }
                        Option("Of July") { Text("Of July") }
                        Option("Of August") { Text("Of August") }
                        Option("Of September") { Text("Of September") }
                        Option("Of October") { Text("Of October") }
                        Option("Of November") { Text("Of November") }
                        Option("Of December") { Text("Of December") }
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
