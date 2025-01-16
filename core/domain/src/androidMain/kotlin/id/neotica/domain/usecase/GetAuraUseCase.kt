package id.neotica.domain.usecase

import androidx.compose.runtime.mutableLongStateOf
import id.neotica.domain.remote.AuraRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GetAuraUseCase(
    private val auraRepo: AuraRepository,
    private val getUserIdUseCase: GetUserIdUseCase,
) {
    private fun currentUserId(): String = getUserIdUseCase.invoke()
    private var initAura = mutableLongStateOf(0)

    operator fun invoke(): Long = initAura.longValue

    init {
        CoroutineScope(Dispatchers.IO).launch { main() }
    }

    private suspend fun main() = withContext(Dispatchers.IO) {
        auraRepo.getAuras(currentUserId()).collect {
            it.data?.aura.let { auraVal ->
                if (auraVal != null) {
                    initAura.longValue = auraVal
                }
            }
        }
    }
}