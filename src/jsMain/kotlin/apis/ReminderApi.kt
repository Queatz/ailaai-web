package apis

import Api
import com.queatz.db.Reminder
import com.queatz.db.ReminderOccurrence
import com.queatz.db.ReminderOccurrences
import io.ktor.client.statement.*
import kotlinx.serialization.Serializable
import kotlin.js.Date


suspend fun Api.reminders(
    onError: suspend (Throwable) -> Unit = {},
    onSuccess: suspend (List<Reminder>) -> Unit = {}
) = get(
    url = "reminders",
    parameters = mapOf("limit" to "100"),
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

suspend fun Api.reminderOccurrences(
    id: String,
    start: Date,
    end: Date,
    onError: suspend (Throwable) -> Unit = {},
    onSuccess: suspend (List<ReminderOccurrences>) -> Unit = {}
) = get(
    url = "/reminders/$id/occurrences",
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
