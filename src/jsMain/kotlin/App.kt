import app.NavPage
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
    var navPage: NavPage = NavPage.Groups
        private set

    init {
        bearerToken.value = localStorage["bearer"]

        val meJson = localStorage["me"]
        if (meJson != null) {
            try {
                me.value = json.decodeFromString<Person>(meJson)
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }

        val navPageJson = localStorage["app.nav"]
        if (navPageJson != null) {
            try {
                navPage = json.decodeFromString<NavPage>(navPageJson)
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    fun setNavPage(navPage: NavPage?) {
        this.navPage = navPage ?: NavPage.Groups
        if (navPage != null) {
            localStorage["app.nav"] = json.encodeToString(navPage)
        } else {
            localStorage.removeItem("app.nav")
        }
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
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }
}
