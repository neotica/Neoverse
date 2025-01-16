package id.neotica.alexandria.screen.goetia

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neotica.alexandria.ktor.GoetiaService
import id.neotica.domain.ApiResult
import id.neotica.domain.model.alexandria.Spirits
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GoetiaViewModel(
    private val apiService: GoetiaService
): ViewModel() {
    private var _spirits: MutableStateFlow<ApiResult<List<Spirits>>> = MutableStateFlow(ApiResult.Loading())
    val spirits = _spirits.asStateFlow()

    init {
        getSpirits()
    }

    private fun getSpirits() = viewModelScope.launch {
        apiService.getSpirits().collect {
            Log.d("spirit", it.toString())
            _spirits.value = it
        }
    }
}