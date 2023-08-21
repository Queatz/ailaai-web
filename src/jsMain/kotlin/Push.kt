import com.queatz.PushData
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.utils.io.charsets.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.w3c.dom.EventSource

val push = Push()

class Push {

    private lateinit var job: Job

    val events = MutableSharedFlow<PushData>()

    fun start(scope: CoroutineScope) {
        job = scope.launch {
            application.bearerToken.first { it != null }
            try {
                http.post("$baseUrl/me/device") {
                    contentType(ContentType.Application.Json.withCharset(Charsets.UTF_8))
                    bearerAuth(application.bearer)
                    setBody(
                        Device(
                            type = DeviceType.Web,
                            token = api.token
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val deferred = CompletableDeferred<Unit>()
            val sse = EventSource("$baseUrl/push/${api.token}")
            val jobScope = this
            sse.onmessage = {
                console.log("Push event:", it)
                jobScope.launch {
                    (it.data as? String)?.let { data ->
                        events.emit(json.decodeFromString(data))
                    } ?: console.log("Not a string:", it.data)
                }
            }
            sse.onerror = {
                console.log(it)
                if (it.eventPhase == EventSource.CLOSED) {
                    deferred.complete(Unit)
                }
            }
            deferred.await()
        }
    }

    fun stop() {
        job.cancel()
    }
}
