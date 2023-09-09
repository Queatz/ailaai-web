package apis

import Api
import Reminder
import ReminderOccurrence
import io.ktor.client.statement.*
import kotlinx.serialization.Serializable
import kotlin.js.Date

@Serializable
class ReminderOccurrences(
    val reminder: Reminder,
    val dates: List<String>,
    val occurrences: List<ReminderOccurrence>,
)

suspend fun Api.reminders(
    onError: suspend (Throwable) -> Unit = {},
    onSuccess: suspend (List<Reminder>) -> Unit = {}
) = get(
    url = "reminders",
    onError = onError,
    onSuccess = onSuccess
)

suspend fun Api.occurrences(
    start: Date,
    end: Date,
    onError: suspend (Throwable) -> Unit = {},
    onSuccess: suspend (List<ReminderOccurrences>) -> Unit = {}
) = get(
    url = "occurrences",
    onError = onError,
    onSuccess = onSuccess,
    parameters = mapOf(
        "start" to start.toISOString(),
        "end" to end.toISOString()
    )
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

suspend fun Api.updateReminder(
    id: String,
    reminder: Reminder,
    onError: suspend (Throwable) -> Unit = {},
    onSuccess: suspend (Reminder) -> Unit = {}
) = post(
    url = "reminders/$id",
    body = reminder,
    onError = onError,
    onSuccess = onSuccess
)

suspend fun Api.updateReminderOccurrence(
    id: String,
    occurrence: Date,
    update: ReminderOccurrence,
    onError: suspend (Throwable) -> Unit = {},
    onSuccess: suspend (ReminderOccurrence) -> Unit = {}
) = post(
    url = "reminders/$id/occurrences/${occurrence.toISOString()}",
    body = update,
    onError = onError,
    onSuccess = onSuccess
)

suspend fun Api.deleteReminderOccurrence(
    id: String,
    occurrence: Date,
    onError: suspend (Throwable) -> Unit = {},
    onSuccess: suspend (HttpResponse) -> Unit = {}
) = post(
    url = "reminders/$id/occurrences/${occurrence.toISOString()}/delete",
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
