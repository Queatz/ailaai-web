package app.page

import androidx.compose.runtime.*
import app.FullPageLayout
import components.IconButton
import focusable
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
fun SchedulePage(view: ScheduleView) {
    Style(SchedulePageStyles)
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

@Composable
fun EventRow(view: ScheduleView, text: String) {
    var done by remember {
        mutableStateOf(false)
    }

    var edit by remember {
        mutableStateOf(false)
    }

    Div({
        classes(SchedulePageStyles.row)

        title("Mark as done")

        onClick {
            done = !done
        }

        focusable()
    }
    ) {
        Div({
            style {
                flex(1)
                display(DisplayStyle.Flex)
                flexDirection(FlexDirection.Column)
            }
        }) {
            Div({
                classes(SchedulePageStyles.rowText)
                style {
                    if (done) {
                        opacity(.5)
                        textDecoration("line-through")
                    }
                }
            }) {
                Text(text)
            }
            Div({
                style {
                    color(Styles.colors.secondary)
                    fontSize(14.px)

                    if (done) {
                        textDecoration("line-through")
                        opacity(.5)
                    }
                }
            }) {
                when (view) {
                    ScheduleView.Daily -> {
                        Text("1:00pm • I am note!")
                    }
                    ScheduleView.Weekly -> {
                        Text("1st, Tuesday, 1:00pm")
                    }
                    ScheduleView.Monthly -> {
                        Text("12th, Tuesday, 1:00pm • I am note!")
                    }
                    ScheduleView.Yearly -> {
                        Text("August 1st, Tuesday, 1:00pm")
                    }
                }
            }
        }
        Div({
            classes(SchedulePageStyles.rowActions)
        }) {
            IconButton("edit", "Edit", styles = {
            }) {
                edit = !edit
            }
            IconButton("clear", "Delete", styles = {
            }) {

            }
        }
    }
}
