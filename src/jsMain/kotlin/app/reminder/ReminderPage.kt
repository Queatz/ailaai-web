package app.reminder

import Reminder
import androidx.compose.runtime.*
import api
import apis.deleteReminder
import apis.reminders
import app.PageTopBar
import app.menu.Menu
import dialog
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.DOMRect
import org.w3c.dom.HTMLElement
import r

@Composable
fun ReminderPage(reminder: Reminder, onDelete: (Reminder) -> Unit) {
    val scope = rememberCoroutineScope()

    var menuTarget by remember {
        mutableStateOf<DOMRect?>(null)
    }

    menuTarget?.let { target ->
        Menu({ menuTarget = null }, target) {
            item("Invite") {

            }

            item("Delete") {
                scope.launch {
                    val result = dialog("Delete this reminder?", confirmButton = "Yes, delete") {
                        Text("You cannot undo this.")
                    }

                    if (result != true) return@launch

                    api.deleteReminder(reminder.id!!) {
                        onDelete(reminder)
                    }
                }
            }
        }
    }

    Div({
        style {
            flex(1)
            display(DisplayStyle.Flex)
            flexDirection(FlexDirection.Column)
            overflowY("auto")
            overflowX("hidden")
        }
    }) {
        Div({
            style {
                padding(1.5.r)
            }
        }) {
            Text("Reminder details")
        }
    }
    PageTopBar(
        reminder.title ?: "new reminder",
        reminder.scheduleText
    ) {
        menuTarget = if (menuTarget == null) (it.target as HTMLElement).getBoundingClientRect() else null
    }
}
