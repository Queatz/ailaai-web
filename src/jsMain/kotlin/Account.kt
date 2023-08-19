import androidx.compose.runtime.*
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.autoFocus
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.paddingLeft
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.dom.Input
import org.khronos.webgl.Uint8Array
import org.w3c.dom.url.URL
import org.w3c.files.Blob
import org.w3c.files.BlobPropertyBag

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

    LaunchedEffect(Unit) {
        val bytes = Qr.createQR("abc", "gif", js("{ scale: 8 }"))
        val blob = Blob(arrayOf(bytes), BlobPropertyBag("image/gif"))
        qrCode = URL.createObjectURL(blob)
    }

    if (qrCode != null) {
        Img(src = qrCode!!)
    }

    Input(InputType.Text) {
        classes(Styles.textarea)
        style {
            width(100.percent)
            paddingLeft(3.cssRem)
        }

        placeholder("Enter transfer code")

        onInput {

        }

        autoFocus()
    }
}
