package id.neotica.world.feat.game

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neotica.domain.ApiResult
import id.neotica.domain.remote.AuraRepository
import id.neotica.domain.remote.FirebaseRepository
import id.neotica.domain.usecase.GetUserIdUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.security.SecureRandom
import kotlin.random.asKotlinRandom

class GameViewModel(
    private val gameRepo: AuraRepository,
    private val getIdUseCase: GetUserIdUseCase,
    private val firebase: FirebaseRepository
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

    fun isLoggedIn(): Boolean {
        return getIdUseCase.invoke() != ""
    }

    init {
        generateTranspose()
        generateTranspose2()
        generateTranspose3()
        fetchAuras()
    }

    fun getItemEvent(itemName: String, contentType: String){
        firebase.itemLog("GameView", itemName, contentType)
    }

    fun sendJancok(aura: Long) = viewModelScope.launch {
        gameRepo.postAuras(aura, currentUserId())
    }

    fun sendAuraPoints(aura: Int) = viewModelScope.launch {
        val tot = _totalAuras.value.data
        if (tot != null) {
            gameRepo.postAuras(tot + aura, currentUserId())
        }
        fetchAuras()
        Log.d("aura", aura.toString())
    }

    private fun fetchAuras() = viewModelScope.launch {
        gameRepo.getAurasDecrypt(currentUserId()).collect {
            Log.d("auraDecrypted VM", it.toString())
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
            "$seven$seven$seven" -> _auraPoints.value = 750
            "$star$star$star" -> _auraPoints.value = 250
            "$shark$shark$shark" -> _auraPoints.value = 150
            "$smile$smile$smile" -> _auraPoints.value = 50
            else -> _auraPoints.value = -10
        }

        firebase.eventLog("GameView", "slot_result", _auraPoints.value.toString())
    }
}