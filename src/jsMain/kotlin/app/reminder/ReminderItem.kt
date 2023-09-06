package app.reminder

import Reminder
import androidx.compose.runtime.Composable
import app.AppStyles
import focusable
import org.jetbrains.compose.web.css.flexGrow
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text

@Composable
fun ReminderItem(reminder: Reminder, selected: Boolean, onSelected: () -> Unit) {
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
                Text(reminder.title ?: "New reminder")
            }
            Div({
                classes(AppStyles.groupItemMessage)
            }) {
                Text("⏰ Every Sunday at 1pm and 9pm until January 1st, 2024")
            }
        }
    }
}
