package app.nav

import Card
import PaddingDefault
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import app.AppStyles
import application
import baseUrl
import components.Icon
import components.IconButton
import http
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.utils.io.charsets.*
import kotlinx.browser.window
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text

@Composable
fun CardsNavPage() {
    val me by application.me.collectAsState()
    val scope = rememberCoroutineScope()
    NavTopBar(me, "Cards") {
        IconButton("add", "New card", styles = {
            marginRight(1.cssRem)
        }) {
            scope.launch {
                val name = window.prompt("Card title")
                if (name == null) return@launch
                try {
                    val card = http.post("$baseUrl/cards") {
                        setBody(Card(name = name))
                        contentType(ContentType.Application.Json.withCharset(Charsets.UTF_8))
                        bearerAuth(application.bearer)
                    }.body<Card>()
//                    reload()
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
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
        NavMenuItem("explore", "Explore") {}
        NavMenuItem("person", "Yours") {}
        NavMenuItem("favorite", "Saved") {}
    }
}
