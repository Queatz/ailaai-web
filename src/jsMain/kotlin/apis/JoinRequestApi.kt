package apis

import Api
import JoinRequest
import JoinRequestAndPerson
import io.ktor.client.statement.*

suspend fun Api.joinRequests(
    onError: suspend (Throwable) -> Unit = {},
    onSuccess: suspend (List<JoinRequestAndPerson>) -> Unit = {}
) = get(
    url = "join-requests",
    onError = onError,
    onSuccess = onSuccess
)

suspend fun Api.myJoinRequests(
    onError: suspend (Throwable) -> Unit = {},
    onSuccess: suspend (List<JoinRequestAndPerson>) -> Unit = {}
) = get(
    url = "me/join-requests",
    onError = onError,
    onSuccess = onSuccess
)

suspend fun Api.newJoinRequest(
    joinRequest: JoinRequest,
    onError: suspend (Throwable) -> Unit = {},
    onSuccess: suspend (HttpResponse) -> Unit = {}
) = post(
    url = "join-requests",
    body = joinRequest,
    onError = onError,
    onSuccess = onSuccess
)

suspend fun Api.acceptJoinRequest(
    joinRequest: String,
    onError: suspend (Throwable) -> Unit = {},
    onSuccess: suspend (HttpResponse) -> Unit = {}
) = post(
    url = "join-requests/$joinRequest/accept",
    onError = onError,
    onSuccess = onSuccess
)

suspend fun Api.deleteJoinRequest(
    joinRequest: String,
    onError: suspend (Throwable) -> Unit = {},
    onSuccess: suspend (HttpResponse) -> Unit = {}
) = post(
    url = "join-requests/$joinRequest/delete",
    onError = onError,
    onSuccess = onSuccess
)
