package id.neotica.world.feat.mining

import androidx.lifecycle.ViewModel
import id.neotica.domain.usecase.GetAuraUseCase

class MiningViewModel(
    private val getAuraUseCase: GetAuraUseCase
): ViewModel() {
    fun getAura() = getAuraUseCase.invoke()
}