import kotlinx.browser.localStorage
import org.w3c.dom.get
import org.w3c.dom.set
import kotlin.random.Random

val api = Api()

class Api {
    val token: String
        get() {
            val token = localStorage["device"]
            return if (token.isNullOrBlank()) {
                (0 until 128).token().also {
                    localStorage["device"] = it
                }
            } else {
                token
            }
        }
}

fun IntRange.token() = joinToString("") { Random.nextInt(35).toString(36) }
