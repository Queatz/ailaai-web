package components

import LinkDeviceToken
import Person
import SignInRequest
import Styles
import TokenResponse
import androidx.compose.runtime.*
import app.softwork.routingcompose.Router
import application
import baseUrl
import http
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.utils.io.charsets.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
import webBaseUrl
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
    val scope = rememberCoroutineScope()
    val router = Router.current

    var qrCode by remember {
        mutableStateOf<String?>(null)
    }
    var status by remember {
        mutableStateOf<String?>(null)
    }
    var qrCodeLinked by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        val linkDeviceToken: String = try {
            http.post("$baseUrl/link-device") {
                contentType(ContentType.Application.Json.withCharset(Charsets.UTF_8))
            }.body<LinkDeviceToken>().token!!
        } catch (e: Throwable) {
            e.printStackTrace()
            status = "Error"
            return@LaunchedEffect
        }
        val bytes = Qr.createQR("$webBaseUrl/link-device/$linkDeviceToken", "gif", js("{ scale: 5 }"))
        val blob = Blob(arrayOf(bytes), BlobPropertyBag("image/gif"))
        qrCode = URL.createObjectURL(blob)
        withTimeoutOrNull(5.minutes) {
            while (!qrCodeLinked) {
                delay(2.seconds)
                try {
                    val link = http.get("$baseUrl/link-device/$linkDeviceToken") {
                        contentType(ContentType.Application.Json.withCharset(Charsets.UTF_8))
                    }.body<LinkDeviceToken>()

                    if (link.person != null) {
                        qrCodeLinked = true
                    }
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
        }
        if (qrCodeLinked) {
            try {
                val signInResponse = http.post("$baseUrl/sign/in") {
                    contentType(ContentType.Application.Json.withCharset(Charsets.UTF_8))
                    setBody(SignInRequest(link = linkDeviceToken))
                }.body<TokenResponse>()

                application.setToken(signInResponse.token)

                val me = http.get("$baseUrl/me") {
                    contentType(ContentType.Application.Json.withCharset(Charsets.UTF_8))
                    bearerAuth(signInResponse.token)
                }.body<Person>()

                application.setMe(me)

                status = "Xin chào, ${me.name}"
                router.navigate("/")
            } catch (e: Throwable) {
                e.printStackTrace()
                status = "Error"
            }
        }
    }

    fun signIn(transferCode: String) {
        scope.launch {
            try {
                val signInResponse = http.post("$baseUrl/sign/in") {
                    contentType(ContentType.Application.Json.withCharset(Charsets.UTF_8))
                    setBody(SignInRequest(code = transferCode))
                }.body<TokenResponse>()

                application.setToken(signInResponse.token)

                val me = http.get("$baseUrl/me") {
                    contentType(ContentType.Application.Json.withCharset(Charsets.UTF_8))
                    bearerAuth(signInResponse.token)
                }.body<Person>()

                application.setMe(me)

                status = "Xin chào, ${me.name}"
                router.navigate("/")
            } catch (e: Throwable) {
                e.printStackTrace()
                status = "Error"
            }
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
                if (!qrCodeLinked && qrCode != null) {
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
                        maxWidth(36.cssRem)
                    }

                    placeholder("Or enter a transfer code here")

                    onInput {
                        if(it.value.length == 16) {
                            signIn(it.value)
                            it.target.value = ""
                        }
                    }

                    autoFocus()
                }
            }
        }
    }
}
