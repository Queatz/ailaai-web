import androidx.compose.runtime.*
import app.nav.NavSearchInput
import kotlinx.browser.document
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import org.jetbrains.compose.web.attributes.autoFocus
import org.jetbrains.compose.web.css.margin
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.renderComposable
import org.w3c.dom.HTMLDialogElement

suspend fun dialog(
    title: String,
    confirmButton: String = "Okay",
    cancelButton: String? = "Cancel",
    cancellable: Boolean = true,
    content: @Composable (resolve: (Boolean?) -> Unit) -> Unit = {}
): Boolean? {
    val result = CompletableDeferred<Boolean?>()
    val dialog = document.createElement("dialog") as HTMLDialogElement
    dialog.classList.add(Styles.modal)
    dialog.onclose = {
        if (!result.isCompleted) {
            result.complete(null)
        }
        dialog.remove()
    }

    if (cancellable) {
        dialog.onclick = { event ->
            if (event.target == dialog) {
                val rect = dialog.getBoundingClientRect()
                val isInDialog = (rect.top <= event.clientY && event.clientY <= rect.top + rect.height &&
                        rect.left <= event.clientX && event.clientX <= rect.left + rect.width)
                if (!isInDialog) {
                    dialog.close()
                }
            }
        }
    }
    document.body?.appendChild(dialog)
    renderComposable(dialog) {
        if (title.isNotBlank()) {
            Header {
                Text(title)
            }
        }
        Section {
            content {
                result.complete(it)
            }
        }
        Footer {
            Button({
                classes(Styles.button)
                onClick {
                    result.complete(true)
                }

                if (cancelButton == null) {
                    autoFocus()

                    ref {
                        it.focus()
                        onDispose {  }
                    }
                }
            }) {
                Text(confirmButton)
            }
            if (cancelButton != null) {
                Button({
                    classes(Styles.textButton)
                    onClick {
                        result.complete(false)
                    }
                }) {
                    Text(cancelButton)
                }
            }
        }
    }

    dialog.showModal()

    return try {
        result.await().also {
            dialog.close()
        }
    } catch (e: CancellationException) {
        e.printStackTrace()
        dialog.close()
        null
    }
}

suspend fun inputDialog(
    title: String,
    placeholder: String = "",
    confirmButton: String = "Okay",
    cancelButton: String? = "Cancel",
    defaultValue: String = "",
    singleLine: Boolean = true
): String? {
    var text: String = defaultValue
    val result = dialog(
        title,
        confirmButton,
        cancelButton,
    ) { resolve ->
        var value by remember {
            mutableStateOf(defaultValue)
        }

        NavSearchInput(
            value,
            {
                value = it
                text = it
            },
            placeholder = placeholder,
            selectAll = true,
            styles = {
                margin(0.r)
            }
        ) {
            resolve(true)
        }
    }

    return if (result == true) text else null
}
