package id.neotica.world.feat.raven

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neotica.domain.ApiResult
import id.neotica.domain.model.raven.Raven
import id.neotica.domain.remote.RavenRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RavenViewModel(
    private val ravenRepo: RavenRepository
): ViewModel() {

    private var _words: MutableStateFlow<ApiResult<List<String>>> = MutableStateFlow(ApiResult.Loading())
    val words = _words.asStateFlow()

    fun postWord(raven: Raven?) = viewModelScope.launch {
        val dummyRaven = raven?: Raven(
            word = "lol",
            value = "yeah haha"
        )
        ravenRepo.addWord(dummyRaven)
    }

    fun getWords(word: String) = viewModelScope.launch {
        val dummyRaven = Raven(
            word = word,
        )
        ravenRepo.getWords(dummyRaven).collect {
            _words.value = it
            Log.d("ravenvm", it.data.toString())
        }
    }
}