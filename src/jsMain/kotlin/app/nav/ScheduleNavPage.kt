package app.nav

import PaddingDefault
import androidx.compose.runtime.*
import application
import org.jetbrains.compose.web.attributes.autoFocus
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.dom.TextArea

@Composable
fun ScheduleNavPage() {
    val me by application.me.collectAsState()

    var note by remember {
        mutableStateOf("")
    }

    NavTopBar(me, "Reminders")

    TextArea(note) {
        classes(Styles.textarea)
        style {
            margin(.5.cssRem, 1.cssRem)
            height(3.5.cssRem)
            maxHeight(6.5.cssRem)
        }

        onKeyDown {
            if (it.key == "Enter" && !it.shiftKey) {
                it.preventDefault()
                it.stopPropagation()
                note = ""
            }
        }

        onInput {
            note = it.value
            it.target.style.height = "0"
            it.target.style.height = "${it.target.scrollHeight + 2}px"
        }

        placeholder("New reminder")

        autoFocus()
    }

    if (note.isNotBlank()) {
        Div({
            style {
                padding(1.cssRem)
            }
        }) {
            Text("Schedule the reminder here")
        }
    }

    // todo this is same as groupsnavpage Should be NavMainContent
    Div({
        style {
            overflowY("auto")
            overflowX("hidden")
            padding(PaddingDefault / 2)
        }
    }) {
        NavMenuItem("routine", "Day") {}
        NavMenuItem("calendar_view_week", "Week") {}
        NavMenuItem("calendar_month", "Month") {}
        NavMenuItem("rotate_right", "Year") {}
    }
}
