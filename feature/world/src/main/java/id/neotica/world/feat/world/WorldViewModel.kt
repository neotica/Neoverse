package id.neotica.world.feat.world

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neotica.domain.ApiResult
import id.neotica.domain.model.Games
import id.neotica.domain.remote.AuraRepository
import id.neotica.domain.usecase.GetUserIdUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WorldViewModel(
    private val gameRepo: AuraRepository,
    private val getIdUseCase: GetUserIdUseCase,
): ViewModel() {
    private val _totalAuras: MutableStateFlow<ApiResult<Games?>> = MutableStateFlow(ApiResult.Loading())
    val totalAuras = _totalAuras.asStateFlow()

    private fun currentUserId(): String = getIdUseCase.invoke()

    init {
        fetchAuras()
    }

    fun fetchAuras() = viewModelScope.launch {
        val auraFromRepo = gameRepo.getAuras(currentUserId())
        _totalAuras.value = auraFromRepo
    }

    fun sendInitialAura(aura: Int) = viewModelScope.launch {
        gameRepo.postAuras(aura, currentUserId())
        fetchAuras()
    }

    fun isLoggedIn(): Boolean {
        return getIdUseCase.invoke() != ""
    }
}