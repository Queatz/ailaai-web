package apis

import Api
import Reminder
import io.ktor.client.statement.*

suspend fun Api.reminders(
    onError: suspend (Throwable) -> Unit = {},
    onSuccess: suspend (List<Reminder>) -> Unit = {}
) = get(
    url = "reminders",
    onError = onError,
    onSuccess = onSuccess
)

suspend fun Api.newReminder(
    reminder: Reminder,
    onError: suspend (Throwable) -> Unit = {},
    onSuccess: suspend (Reminder) -> Unit = {}
) = post(
    url = "reminders",
    body = reminder,
    onError = onError,
    onSuccess = onSuccess
)


suspend fun Api.deleteReminder(
    reminderId: String,
    onError: suspend (Throwable) -> Unit = {},
    onSuccess: suspend (HttpResponse) -> Unit = {}
) = post(
    url = "reminders/${reminderId}/delete",
    onError = onError,
    onSuccess = onSuccess
)
