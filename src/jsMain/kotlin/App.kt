import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.utils.io.charsets.*
import kotlinx.browser.localStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.encodeToString
import org.w3c.dom.get
import org.w3c.dom.set

val application = Application()

class Application {
    val me = MutableStateFlow<Person?>(null)
    val bearerToken = MutableStateFlow<String?>(null)

    val bearer: String get() = bearerToken.value!!

    init {
        val meJson = localStorage["me"]
        if (meJson != null) {
            try {
                me.value = json.decodeFromString<Person>(meJson)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        bearerToken.value = localStorage["bearer"]
    }

    fun setMe(me: Person?) {
        this.me.value = me
        if (me != null) {
            localStorage["me"] = json.encodeToString(me)
        } else {
            localStorage.removeItem("me")
        }
    }

    fun setToken(token: String?) {
        this.bearerToken.value = token
        if (token != null) {
            localStorage["bearer"] = token
        } else {
            localStorage.removeItem("bearer")
        }
    }

    suspend fun sync() {
        if (me.value != null && bearerToken.value != null) {
            try {
                setMe(
                    http.get("$baseUrl/me") {
                        contentType(ContentType.Application.Json.withCharset(Charsets.UTF_8))
                        bearerAuth(bearerToken.value!!)
                    }.body<Person>()
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
