package id.neotica.alexandria.screen.admin.warning

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neotica.domain.ApiResult
import id.neotica.domain.model.admin.WarningResponse
import id.neotica.domain.remote.admin.WarningRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WarningHubViewModel(
    private val repo: WarningRepository
): ViewModel() {

    private val _warning = MutableStateFlow<List<WarningResponse>?>(null)
    val warning = _warning.asStateFlow()

    private var _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private var _message = MutableStateFlow<String?>(null)
    val message = _message.asStateFlow()

    init {
        getWarning()
    }

    fun getWarning() = viewModelScope.launch {
        repo.getWarnings().collect {
            when(it) {
                is ApiResult.Loading -> _isLoading.value = true
                is ApiResult.Success -> {
                    _isLoading.value = false
                    _warning.value = it.data?.content
                }
                is ApiResult.Error -> {
                    _isLoading.value = false
                    _message.value = it.errorMessage
                }
            }
        }
    }
}