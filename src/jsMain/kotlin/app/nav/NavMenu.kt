package app.nav

import androidx.compose.runtime.Composable
import app.AppStyles
import app.messaages.inList
import components.Icon
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text

@Composable fun NavMenuItem(icon: String, title: String, selected: Boolean = false, onClick: () -> Unit) {
    Div({
        classes(
            listOf(AppStyles.groupItem, AppStyles.navMenuItem) + if (selected) AppStyles.groupItemSelected.inList() else emptyList()
        )
        onClick {
            onClick()
        }
    }) {
        Icon(icon)
        Text(title)
    }
}
