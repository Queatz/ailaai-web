import androidx.compose.runtime.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.utils.io.charsets.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeoutOrNull
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.autoFocus
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.Text
import org.khronos.webgl.Uint8Array
import org.w3c.dom.url.URL
import org.w3c.files.Blob
import org.w3c.files.BlobPropertyBag
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@JsModule("@paulmillr/qr")
@JsNonModule
external object Qr {
    @JsName("default")
    fun createQR(text: String, output: String = definedExternally, opts: dynamic = definedExternally): Uint8Array
}

@Composable
fun SigninPage() {
    var qrCode by remember {
        mutableStateOf<String?>(null)
    }
    var status by remember {
        mutableStateOf<String?>(null)
    }

    LaunchedEffect(Unit) {
        val linkDeviceToken = http.post("$baseUrl/link-device") {
            contentType(ContentType.Application.Json.withCharset(Charsets.UTF_8))
        }.body<LinkDeviceToken>().token
        val bytes = Qr.createQR("$webBaseUrl/link-device/$linkDeviceToken", "gif", js("{ scale: 8 }"))
        val blob = Blob(arrayOf(bytes), BlobPropertyBag("image/gif"))
        qrCode = URL.createObjectURL(blob)
        val isLinked: Boolean? = withTimeoutOrNull(5.minutes) {
            while (true) {
                delay(2.seconds)
                val link = http.get("$baseUrl/link-device/$linkDeviceToken") {
                    contentType(ContentType.Application.Json.withCharset(Charsets.UTF_8))
                }.body<LinkDeviceToken>()

                if (link.person != null) {
                    return@withTimeoutOrNull true
                }
            }
            return@withTimeoutOrNull false
        }
        if (isLinked == true) {
            val signInResponse = http.post("$baseUrl/sign/in") {
                contentType(ContentType.Application.Json.withCharset(Charsets.UTF_8))
                setBody(SignInRequest(link = linkDeviceToken))
            }.body<TokenResponse>()

            val me = http.get("$baseUrl/me") {
                contentType(ContentType.Application.Json.withCharset(Charsets.UTF_8))
                bearerAuth(signInResponse.token)
            }.body<Person>()

            status = "Xin chào, ${me.name}"
        }
    }

    Div({
        classes(Styles.mainContent)
    }) {
        Div({
            classes(Styles.navContainer)
            style {
                width(1200.px)
                flexShrink(1f)
                alignSelf(AlignSelf.Center)
                marginBottom(1.cssRem)
            }
        }) {
            Div({
                classes(Styles.navContent)
                style {
                    padding(1.cssRem)
                    alignItems(AlignItems.Center)
                }
            }) {
                if (qrCode != null) {
                    Img(src = qrCode!!)
                }

                Div({
                    style {
                        color(Styles.colors.secondary)
                        paddingBottom(1.cssRem)
                    }
                }) {
                    Text(status ?: "Use your phone to scan the QR code.")
                }

                Input(InputType.Text) {
                    classes(Styles.textarea)
                    style {
                        width(100.percent)
                    }

                    placeholder("Or enter a transfer code here")

                    onInput {

                    }

                    autoFocus()
                }
            }
        }
    }
}
