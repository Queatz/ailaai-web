package app.reminder

import androidx.compose.runtime.*
import app.page.ReminderEventType
import app.page.SchedulePageStyles
import app.page.ScheduleView
import components.IconButton
import focusable
import lib.format
import notBlank
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text
import kotlin.js.Date

@Composable
fun EventRow(
    view: ScheduleView,
    date: Date,
    event: ReminderEventType,
    done: Boolean,
    text: String,
    note: String,
    onDone: (Boolean) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    Div({
        classes(SchedulePageStyles.row)

        title("Mark as done")

        onClick {
            onDone(!done)
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
                Text(
                    text + when (event) {
                        ReminderEventType.Start -> " starts"
                        ReminderEventType.End -> " ends"
                        ReminderEventType.Occur -> ""
                    }
                )
            }
            Div({
                style {
                    color(Styles.colors.secondary)
                    fontSize(14.px)
                    whiteSpace("pre-wrap")

                    if (done) {
                        textDecoration("line-through")
                        opacity(.5)
                    }
                }
            }) {
                val details = note.notBlank?.let { " • $it" } ?: ""

                when (view) {
                    ScheduleView.Daily -> {
                        Text("${format(date, "h:mm a")}$details")
                    }

                    ScheduleView.Weekly -> {
                        Text("${format(date, "MMMM do, EEEE, h:mm a")}$details")
                    }

                    ScheduleView.Monthly -> {
                        Text("${format(date, "do, EEEE, h:mm a")}$details")
                    }

                    ScheduleView.Yearly -> {
                        Text("${format(date, "MMMM do, EEEE, h:mm a")}$details")
                    }
                }
            }
        }
        Div({
            classes(SchedulePageStyles.rowActions)
        }) {
            IconButton("edit", "Edit", styles = {
            }) {
                it.stopPropagation()
                onEdit()
            }
            IconButton("clear", "Delete", styles = {
            }) {
                it.stopPropagation()
                onDelete()
            }
        }
    }
}
