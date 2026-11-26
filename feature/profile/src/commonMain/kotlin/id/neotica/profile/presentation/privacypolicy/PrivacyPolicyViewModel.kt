package id.neotica.profile.presentation.privacypolicy

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PrivacyPolicyViewModel : ViewModel() {
    private val _state = MutableStateFlow(PrivacyPolicyState())
    val state = _state.asStateFlow()

    fun onLoadingChanged(isLoading: Boolean) {
        _state.update { it.copy(isLoading = isLoading) }
    }
}
