package id.neotica.neoversewear.presentation.screen.world

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neotica.domain.ApiResult
import id.neotica.domain.model.tavern.TavernPost
import id.neotica.domain.remote.AuraRepository
import id.neotica.domain.remote.FeedRepository
import id.neotica.domain.usecase.GetUserIdUseCase
import id.neotica.domain.usecase.GetUserNameUseCase
import id.neotica.domain.utils.randomUUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.security.SecureRandom
import kotlin.random.asKotlinRandom

class WBarViewModel(
    private val gameRepo: AuraRepository,
    private val feedRepo: FeedRepository,
    private val getIdUseCase: GetUserIdUseCase,
    private val getUserNameUseCase: GetUserNameUseCase,
): ViewModel() {
    private val _randomNum = MutableStateFlow("")
    val randomNum = _randomNum.asStateFlow()
    private val _randomNum2 = MutableStateFlow("")
    val randomNum2 = _randomNum2.asStateFlow()
    private val _randomNum3 = MutableStateFlow("")
    val randomNum3 = _randomNum3.asStateFlow()

    private val _auraPoints = MutableStateFlow(0)
    val auraPoints = _auraPoints.asStateFlow()

    private val _totalAuras: MutableStateFlow<ApiResult<Long?>> = MutableStateFlow(ApiResult.Loading())
    val totalAuras = _totalAuras.asStateFlow()

    private fun currentUserId(): String = getIdUseCase.invoke()
    private fun currentUsername(): String = getUserNameUseCase.invoke()

    fun isLoggedIn(): Boolean {
        return getIdUseCase.invoke() != ""
    }

    init {
        generateTranspose()
        generateTranspose2()
        generateTranspose3()
        fetchAuras()
    }

    fun sendAuraPoints(aura: Int) = viewModelScope.launch {
        val tot = _totalAuras.value.data
        if (tot != null) {
            gameRepo.postAuras(tot + aura, currentUserId())
        }
        fetchAuras()
    }

    private fun fetchAuras() = viewModelScope.launch {
        gameRepo.getAurasDecrypt(currentUserId()).collect {
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

    private fun generateTranspose2() = viewModelScope.launch {
        val randomValue = SecureRandom().asKotlinRandom(). nextInt(1,80)
        _randomNum2.value = when (randomValue) {
            in 1..15 -> "7\uFE0F"
            in 21..40 -> "\uD83D\uDE0A"
            in 41..60 -> "⭐\uFE0F"
            else -> "\uD83E\uDD88"
        }
    }
    private fun generateTranspose3() = viewModelScope.launch {
        val randomValue = SecureRandom().asKotlinRandom(). nextInt(1,80)
        _randomNum3.value = when (randomValue) {
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
            "$seven$seven$seven" -> {
                sendScore(750)
                _auraPoints.value = 750
            }
            "$star$star$star" -> {
                sendScore(250)
                _auraPoints.value = 250
            }
            "$shark$shark$shark" -> {
                sendScore(150)
                _auraPoints.value = 150
            }
            "$smile$smile$smile" -> {
                sendScore(50)
                _auraPoints.value = 50
            }
            else -> _auraPoints.value = -10
        }
    }

    private fun sendScore(aura: Long) = viewModelScope.launch {
        val message: String = when(aura) {
            50L -> "${currentUsername()} has won $aura auras!"
            100L -> "${currentUsername()} has won $aura auras!"
            150L -> "${currentUsername()} has won $aura auras!"
            250L -> "${currentUsername()} has won $aura auras!"
            750L -> "JACKPOT!!! ${currentUsername()} has won $aura auras!!!"
            else -> "${currentUsername()} has won $aura auras!"
        }
        val post = TavernPost(
            id = randomUUID(),
            creatorId = "System",
            creatorName = "System",
            message = message
        )
        feedRepo.postLogs(post)
    }
}