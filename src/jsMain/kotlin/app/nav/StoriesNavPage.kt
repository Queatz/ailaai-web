package app.nav

import PaddingDefault
import Story
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import application
import baseUrl
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
fun StoriesNavPage() {
    val me by application.me.collectAsState()
    val scope = rememberCoroutineScope()
    NavTopBar(me, "Stories") {
        IconButton("add", "New story", styles = {
            marginRight(1.cssRem)
        }) {
            scope.launch {
                val title = window.prompt("Story title")
                if (title == null) return@launch
                try {
                    val story = http.post("$baseUrl/stories") {
                        setBody(Story(title = title))
                        contentType(ContentType.Application.Json.withCharset(Charsets.UTF_8))
                        bearerAuth(application.bearer)
                    }.body<Story>()
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
//        NavMenuItem("favorite", "Saved") {}
    }
}
