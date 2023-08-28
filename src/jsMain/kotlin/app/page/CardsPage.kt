package app.page

import Card
import androidx.compose.runtime.*
import application
import baseUrl
import components.CardItem
import components.Loading
import http
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.utils.io.charsets.*
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.dom.Div

@Composable
fun CardsPage() {
    Style(CardsPageStyles)
    val me by application.me.collectAsState()
    var cards by remember {
        mutableStateOf(listOf<Card>())
    }
    var isLoading by remember {
        mutableStateOf(false)
    }

    suspend fun reload() {
        if (me == null) return
        try {
            cards = http.get("$baseUrl/me/cards") {
                contentType(ContentType.Application.Json.withCharset(Charsets.UTF_8))
                bearerAuth(application.bearer)
            }.body()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        isLoading = false
    }

    LaunchedEffect(me) {
        reload()
    }

    if (isLoading) {
        Loading()
    } else {
        Div(
            {
                classes(CardsPageStyles.layout)
            }
        ) {
            cards.forEach {
                CardItem(it)
            }
        }
    }
}
