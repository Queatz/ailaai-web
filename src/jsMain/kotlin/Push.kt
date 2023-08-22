import com.queatz.PushData
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.utils.io.charsets.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import org.w3c.dom.EventSource
import kotlin.time.Duration.Companion.seconds

val push = Push()

class Push {

    private lateinit var job: Job

    val events = MutableSharedFlow<PushData>()
    val reconnect = MutableSharedFlow<Unit>()

    fun start(scope: CoroutineScope) {
        job = scope.launch {
            while (true) {
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
                } catch (e: Throwable) {
                    e.printStackTrace()
                    delay(3.seconds)
                    continue
                }
                val deferred = CompletableDeferred<Unit>()
                val sse = EventSource("$baseUrl/push/${api.token}")
                val jobScope = this
                sse.onmessage = {
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
                sse.onopen = {
                    jobScope.launch {
                        reconnect.emit(Unit)
                    }
                }
                deferred.await()
                delay(3.seconds)
            }
        }
    }

    fun stop() {
        job.cancel()
    }
}
