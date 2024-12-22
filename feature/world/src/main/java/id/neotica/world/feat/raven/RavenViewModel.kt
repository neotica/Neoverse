package id.neotica.world.feat.raven

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neotica.domain.model.raven.Raven
import id.neotica.domain.remote.RavenRepository
import kotlinx.coroutines.launch

class RavenViewModel(
    private val ravenRepo: RavenRepository
): ViewModel() {
    fun postWord(raven: Raven?) = viewModelScope.launch {
        val dummyRaven = raven?: Raven(
            word = "lol",
            value = "yeah haha"
        )
        ravenRepo.addWord(dummyRaven)
    }

    fun getWords() = viewModelScope.launch {
        val dummyRaven = Raven(
            word = "lol",
        )
        ravenRepo.getWords(dummyRaven).collect {
            Log.d("ravenvm", it.toString())
        }
    }
}