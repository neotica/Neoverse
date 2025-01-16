package id.neotica.world.feat.kanye

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neotica.domain.ApiResult
import id.neotica.domain.model.kanye.KanyeQuotes
import id.neotica.domain.remote.KanyeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class KanyeViewModel(
    private val repo: KanyeRepository
): ViewModel() {
    private val _quote = MutableStateFlow<ApiResult<KanyeQuotes>>(ApiResult.Loading())
    val quote = _quote.stateIn(viewModelScope, SharingStarted.Lazily, ApiResult.Loading())

    fun fetchQuote() = viewModelScope.launch {
        repo.getQuote()
            .onStart { _quote.value = ApiResult.Loading() }
            .collect {
            _quote.value = it
        }
    }
}