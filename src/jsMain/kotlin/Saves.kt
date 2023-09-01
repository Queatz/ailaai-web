import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.utils.io.charsets.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

val saves = Saves()

class Saves {

    val cards = MutableStateFlow<Set<Card>>(emptySet())

    fun start(scope: CoroutineScope) {
        scope.launch {
            reload()
        }
    }

    suspend fun reload() {
        application.bearerToken.first { it != null }
        try {
            val result = http.get("$baseUrl/me/saved") {
                contentType(ContentType.Application.Json.withCharset(Charsets.UTF_8))
                bearerAuth(application.bearer)
            }.body<List<SaveAndCard>>()
            cards.emit(result.mapNotNull { it.card }.toSet())
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    suspend fun save(id: String) {
        try {
            http.post("$baseUrl/cards/$id/save") {
                contentType(ContentType.Application.Json.withCharset(Charsets.UTF_8))
                bearerAuth(application.bearer)
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        reload()
    }

    suspend fun unsave(id: String) {
        try {
            http.post("$baseUrl/cards/$id/unsave") {
                contentType(ContentType.Application.Json.withCharset(Charsets.UTF_8))
                bearerAuth(application.bearer)
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        reload()
    }
}
