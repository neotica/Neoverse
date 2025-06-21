package id.neotica.tavern

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neotica.domain.ApiResult
import id.neotica.domain.model.chat.ParentNeo
import id.neotica.domain.model.tavern.TavernPost
import id.neotica.domain.remote.AuraRepository
import id.neotica.domain.remote.FeedRepository
import id.neotica.domain.remote.FirebaseRepository
import id.neotica.domain.usecase.GetUserIdUseCase
import id.neotica.domain.usecase.GetUserNameUseCase
import id.neotica.domain.utils.randomUUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class TavernViewModel(
    private val gameRepo: AuraRepository,
    private val feedRepo: FeedRepository,
    private val getIdUseCase: GetUserIdUseCase,
    private val getUserNameUseCase: GetUserNameUseCase,
    private val firebase: FirebaseRepository
): ViewModel() {
    private val _randomNum = MutableStateFlow("")
    val randomNum = _randomNum.asStateFlow()

    private val _auraPoints = MutableStateFlow(0)
    val auraPoints = _auraPoints.asStateFlow()

    private val _totalAuras: MutableStateFlow<ApiResult<Long?>> = MutableStateFlow(ApiResult.Loading())
    val totalAuras = _totalAuras.asStateFlow()

    private var _whispers: MutableStateFlow<ApiResult<List<ParentNeo<TavernPost>>>> = MutableStateFlow(ApiResult.Loading())
    val whispers = _whispers.asStateFlow()

    private var _logs: MutableStateFlow<ApiResult<List<ParentNeo<TavernPost>>>> = MutableStateFlow(ApiResult.Loading())
    val logs = _logs.asStateFlow()

    private val resultMapping = mapOf(
        "7\uFE0F7\uFE0F7\uFE0F" to 750,
        "⭐\uFE0F⭐\uFE0F⭐\uFE0F" to 250,
        "\uD83E\uDD88\uD83E\uDD88\uD83E\uDD88" to 150,
        "\uD83D\uDE0A\uD83D\uDE0A\uD83D\uDE0A" to 50,
    )

    private fun currentUserId(): String = getIdUseCase.invoke()
    private fun currentUsername(): String = getUserNameUseCase.invoke()

    fun isLoggedIn(): Boolean {
        return getIdUseCase.invoke() != ""
    }

    init {
        generateTranspose(1)
        generateTranspose(2)
        generateTranspose(3)
        fetchAuras()
        getWhispers()
        getLogs()
    }

    fun getItemEvent(itemName: String, contentType: String){
        firebase.itemLog("GameView", itemName, contentType)
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

    private fun generateTranspose(transposeNumber: Int) = viewModelScope.launch {
        val emojis = listOf("7\uFE0F", "\uD83D\uDE0A", "⭐\uFE0F", "\uD83E\uDD88")
        val weights = listOf(10, 45, 25, 20)
        fun weightedRandom(items: List<String>, weights: List<Int>): String {
            val totalWeight = weights.sum()
            val randomNumber = Random.nextInt(1, totalWeight + 1)
            var cumulativeWeight = 0

            for (i in items.indices) {
                cumulativeWeight += weights[i]
                if (randomNumber <= cumulativeWeight) {
                    return items[i]
                }
            }
            return items.last()
        }

        val result = weightedRandom(emojis, weights)
        when (transposeNumber) {
            1 -> _randomNum.value = result
            else -> Log.e("generateTranspose", "Invalid transpose number: $transposeNumber")
        }
    }

    fun getAuraPoints(result: String) = viewModelScope.launch {
        val points = resultMapping[result]
        if (points != null) {
            sendScore(points.toLong())
            _auraPoints.value = points
        } else {
            _auraPoints.value = -10
        }
        firebase.eventLog("GameView", "slot_result", _auraPoints.value.toString())
    }

    fun sendPost(message: String) = viewModelScope.launch {
        if (currentUsername() != "tarakh") {
            val post = TavernPost(
                id = randomUUID(),
                creatorId = currentUserId(),
                creatorName = currentUsername(),
                message = message
            )
            val tot = _totalAuras.value.data
            if (tot != null) {
                feedRepo.postWhisper(post)
                sendAuraPoints(-5)
                fetchAuras()
            }
            getWhispers()
        }
    }

    private fun sendScore(aura: Long) = viewModelScope.launch {
        val message: String = when(aura) {
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
        getWhispers()
        getLogs()
    }

    private fun getWhispers() = viewModelScope.launch {
        feedRepo.getWhispers().collect {
            _whispers.value = it
        }
    }

    private fun getLogs() = viewModelScope.launch {
        feedRepo.getLogs().collect {
            _logs.value = it
        }
    }
}