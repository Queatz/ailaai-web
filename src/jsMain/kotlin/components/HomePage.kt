package components

import PaddingDefault
import Strings.homeTagline
import Styles
import androidx.compose.runtime.Composable
import appString
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*

@Composable
fun HomePage() {
    Div({
        style {
            property("margin", "$PaddingDefault auto")
            maxWidth(1200.px)
            padding(0.cssRem, 1.cssRem, 1.cssRem, 1.cssRem)
            fontSize(22.px)
            lineHeight("1.5")
            minHeight(100.vh)
        }
    }) {
        Div({
            classes(Styles.mainHeader)
        }) {
            Div {
                Text(appString { homeTagline })
            }
        }
        H3 {
            Text(appString { homeAboutTitle })
        }
        Div {
            Text(appString { homeAboutDescription })
            Br()
            Br()
        }
        Div {
            Text(appString { toJoinThePlatform })
            A("mailto:jacobaferrero@gmail.com?subject=${appString { inviteEmailSubject }}") {
                Text(appString { inlineSendMeAnEmail })
            }
            Text(" ${appString { engageToday }}")
        }
        A("/ailaai.apk", {
            style {
                display(DisplayStyle.InlineBlock)
                padding(1.cssRem, 2.cssRem)
                marginTop(PaddingDefault * 2)
                fontWeight(700)
                fontSize(18.px)
                borderRadius(2.cssRem)
                color(Color.white)
                textDecoration("none")
                textAlign("center")
                property("box-shadow", "2px 2px 8px rgba(0, 0, 0, .25)")
                backgroundColor(Styles.colors.primary)
                background("linear-gradient(rgb(49, 171, 213), rgb(0, 102, 137))")
            }
        }) {
            Span { Text(" ${appString { downloadApp }}") }
            Br()
            Span({
                style {
                    opacity(.75f)
                    fontSize(80.percent)
                }
            }) { Text(appString { appTagline }) }
        }
        listOf(
            appString { peopleToKnow } to listOf("11389583", "11156377", "10455696", "12319827", "9914441").shuffled().take(3),
            appString { placesToKnow } to listOf("9879608", "10102613"),
            appString { thingsToKnow } to listOf("2181697"),
        ).forEach { (category, cards) ->
            H3 {
                Text(category)
            }
            Div({
                classes(Styles.mainContentCards)
            }) {
                cards.forEach { cardId ->
                    CardItem(cardId)
                }
            }
        }
    }
}
