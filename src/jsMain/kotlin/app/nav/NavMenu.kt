package app.nav

import androidx.compose.runtime.Composable
import app.AppStyles
import components.Icon
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text

@Composable fun NavMenuItem(icon: String, title: String, onClick: () -> Unit) {
    Div({
        classes(AppStyles.groupItem, AppStyles.navMenuItem)
        onClick {
            onClick()
        }
    }) {
        Icon(icon)
        Text(title)
    }
}
