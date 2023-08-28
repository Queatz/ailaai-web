import kotlinx.browser.localStorage
import kotlinx.serialization.Serializable
import org.w3c.dom.get
import org.w3c.dom.set
import kotlin.random.Random

val api = Api()

@Serializable
data class TokenResponse (
    val token: String
)

@Serializable
data class SignUpRequest(
    val code: String?
)

@Serializable
data class SignInRequest(
    val code: String? = null,
    val link: String? = null
)

@Serializable
data class CreateGroupBody(val people: List<String>, val reuse: Boolean = false)


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
