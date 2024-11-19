package id.neotica.neoversewear.presentation.screen.world

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neotica.domain.ApiResult
import id.neotica.domain.model.Games
import id.neotica.domain.remote.AuraRepository
import id.neotica.domain.usecase.GetUserIdUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.security.SecureRandom
import kotlin.random.asKotlinRandom

class WBarViewModel(
    private val gameRepo: AuraRepository,
    private val getIdUseCase: GetUserIdUseCase
): ViewModel() {
    private val _randomNum = MutableStateFlow("")
    val randomNum = _randomNum.asStateFlow()

    private val _auraPoints = MutableStateFlow(0)
    val auraPoints = _auraPoints.asStateFlow()

    private val _totalAuras: MutableStateFlow<ApiResult<Games?>> = MutableStateFlow(ApiResult.Loading())
    val totalAuras = _totalAuras.asStateFlow()

    private fun currentUserId(): String = getIdUseCase.invoke()

    fun isLoggedIn(): Boolean {
        return getIdUseCase.invoke() != ""
    }

    init {
        generateTranspose()
        fetchAuras()
    }

    fun sendAuraPoints(aura: Int) = viewModelScope.launch {
        val tot = _totalAuras.value.data?.aura
        if (tot != null) {
            gameRepo.postAuras(tot + aura.toLong(), currentUserId())
        }
        fetchAuras()
    }

    private fun fetchAuras() = viewModelScope.launch {
        gameRepo.getAuras(currentUserId()).collect {
            _totalAuras.value = it
        }
    }

    private fun generateTranspose() = viewModelScope.launch {
        val randomValue = SecureRandom().asKotlinRandom(). nextInt(1,80)
        _randomNum.value = when (randomValue) {
            in 1..15 -> "7\uFE0F"
            in 21..40 -> "\uD83D\uDE0A"
            in 41..60 -> "⭐\uFE0F"
            else -> "\uD83E\uDD88"
        }
    }

    fun getAuraPoints(result: String) = viewModelScope.launch {
        val seven = "7\uFE0F"
        val star = "⭐\uFE0F"
        val smile = "\uD83D\uDE0A"
        val shark = "\uD83E\uDD88"

        when(result) {
            "$seven$seven$seven" -> _auraPoints.value = 500
            "$star$star$star" -> _auraPoints.value = 200
            "$shark$shark$shark" -> _auraPoints.value = 100
            "$smile$smile$smile" -> _auraPoints.value = 50
            else -> _auraPoints.value = -10
        }
    }
}