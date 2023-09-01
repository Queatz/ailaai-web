import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

val saves = Saves()

class Saves {

    val cards = MutableStateFlow<Set<Card>>(emptySet())

    fun start(scope: CoroutineScope) {
        scope.launch {
            reload()
        }
    }

    suspend fun reload() {
        application.bearerToken.first { it != null }
        api.saved {
            cards.emit(it.mapNotNull { it.card }.toSet())
        }
    }

    suspend fun save(id: String) {
        api.save(id)
        reload()
    }

    suspend fun unsave(id: String) {
        api.unsave(id)
        reload()
    }
}
