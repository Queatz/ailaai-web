package app.reminder

import androidx.compose.runtime.*
import app.page.SchedulePageStyles
import app.page.ScheduleView
import components.IconButton
import focusable
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text

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
