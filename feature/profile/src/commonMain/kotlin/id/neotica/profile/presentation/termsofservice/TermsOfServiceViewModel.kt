package id.neotica.profile.presentation.termsofservice

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TermsOfServiceViewModel : ViewModel() {
    private val _state = MutableStateFlow(TermsOfServiceState())
    val state = _state.asStateFlow()

    fun onLoadingChanged(isLoading: Boolean) {
        _state.update { it.copy(isLoading = isLoading) }
    }
}
