package id.neotica.alexandria.screen.admin.warning.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import id.neotica.alexandria.domain.admin.WarningRepository
import id.neotica.domain.ApiResult
import id.neotica.domain.model.admin.WarningResponse
import id.neotica.routes.AlexandriaScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WarningDetailViewModel(
    val repo: WarningRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val _warning = MutableStateFlow<WarningResponse?>(null)
    val warning = _warning.asStateFlow()

    private var _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private var _message = MutableStateFlow<String?>(null)
    val message = _message.asStateFlow()

    private var _navigateBack = MutableStateFlow(false)
    val navigateBack = _navigateBack.asStateFlow()

    private val args = savedStateHandle.toRoute<AlexandriaScreen.WarningDetailScreen>()

    init {
        getDetail(args.id)
    }

    fun getDetail(id: String) = viewModelScope.launch {
        repo.getWarning(id).collect {
            when(it) {
                is ApiResult.Loading -> {_isLoading.value = true}
                is ApiResult.Success -> {
//                    Log.d("WarningDetailViewModel", "getDetail: ${it.data}")
                    _isLoading.value = false
                    _warning.value = it.data
                }
                is ApiResult.Error -> {
//                    Log.d("WarningDetailViewModel", "Error: ${it.errorMessage}")
                    _isLoading.value = false
                    _message.value = it.errorMessage
                }
            }
        }
    }

    fun deleteWarning(id: String) = viewModelScope.launch {
        repo.deleteWarning(id).collect {
            when(it) {
                is ApiResult.Loading -> {
                    _navigateBack.value = false
                    _isLoading.value = true
                }
                is ApiResult.Success -> {
//                    Log.d("WarningDetailViewModel", "getDetail: ${it.data}")
                    _isLoading.value = false
                    _message.value = it.data
                    _navigateBack.value = true
                }
                is ApiResult.Error -> {
//                    Log.d("WarningDetailViewModel", "Error: ${it.errorMessage}")
                    _isLoading.value = false
                    _message.value = it.errorMessage
                }
            }
        }
    }
}