package app

import androidx.compose.runtime.Composable
import components.IconButton
import org.jetbrains.compose.web.css.Color
import org.jetbrains.compose.web.css.backgroundColor
import org.jetbrains.compose.web.css.color
import org.jetbrains.compose.web.css.flexShrink
import org.jetbrains.compose.web.dom.Div

@Composable
fun AppBottomBar(nav: NavPage, onNavClick: (NavPage) -> Unit) {
    Div({
        classes(AppStyles.bottomBar)
        style {
            flexShrink(0)
        }
    }) {
        IconButton("people", "Groups", styles = {
            if (nav == NavPage.Groups) {
                backgroundColor(Styles.colors.primary)
                color(Color.white)
            }
        }) {
            onNavClick(NavPage.Groups)
        }
        IconButton("schedule", "Reminders", styles = {
            if (nav == NavPage.Schedule) {
                backgroundColor(Styles.colors.primary)
                color(Color.white)
            }
        }) {
            onNavClick(NavPage.Schedule)
        }
        IconButton("travel_explore", "Pages", styles = {
            if (nav == NavPage.Cards) {
                backgroundColor(Styles.colors.primary)
                color(Color.white)
            }
        }) {
            onNavClick(NavPage.Cards)
        }
        IconButton("explore", "Stories", styles = {
            if (nav == NavPage.Stories) {
                backgroundColor(Styles.colors.primary)
                color(Color.white)
            }
        }) {
            onNavClick(NavPage.Stories)
        }
    }
}
