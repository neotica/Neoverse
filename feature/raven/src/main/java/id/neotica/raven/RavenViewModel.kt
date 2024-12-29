package id.neotica.raven

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neotica.domain.ApiResult
import id.neotica.domain.model.raven.Raven
import id.neotica.domain.remote.RavenRepository
import id.neotica.domain.usecase.GetUserIdUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RavenViewModel(
    private val ravenRepo: RavenRepository,
    private val getUserIdUseCase: GetUserIdUseCase
): ViewModel() {

    val currentUser = getUserIdUseCase.invoke()

    private var _words: MutableStateFlow<ApiResult<List<String>>> = MutableStateFlow(ApiResult.Loading())
    val words = _words.asStateFlow()

    fun postWord(raven: Raven) = viewModelScope.launch {
        ravenRepo.addWord(raven)
    }

    fun getWords(word: String) = viewModelScope.launch {
        val dummyRaven = Raven(
            word = word,
        )
        ravenRepo.getWords(dummyRaven).collect {
            _words.value = it
        }
    }
}