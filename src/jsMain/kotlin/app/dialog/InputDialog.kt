package app.dialog

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import app.nav.NavSearchInput
import application
import org.jetbrains.compose.web.css.margin
import r

suspend fun inputDialog(
    title: String,
    placeholder: String = "",
    confirmButton: String = application.appString { okay },
    cancelButton: String? = application.appString { cancel },
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
            },
            onDismissRequest = {
                resolve(false)
            }
        ) {
            resolve(true)
        }
    }

    return if (result == true) text else null
}
