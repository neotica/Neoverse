package id.neotica.alexandria.screen.bloodyage

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neotica.domain.ApiResult
import id.neotica.domain.model.alexandria.BloodyAgeChapters
import id.neotica.domain.remote.AlexandriaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BloodyAgeViewModel(
    private val alexandriaRepo: AlexandriaRepository
): ViewModel() {
    private var _chapters: MutableStateFlow<ApiResult<List<BloodyAgeChapters>>> = MutableStateFlow(ApiResult.Loading())
    val chapters = _chapters.asStateFlow()

    private var _chapterDetail: MutableStateFlow<ApiResult<String>> = MutableStateFlow(ApiResult.Loading())
    val chapterDetail = _chapterDetail.asStateFlow()

    init {
        getChapters()
    }

    private fun getChapters() = viewModelScope.launch {
        alexandriaRepo.getBloodyAge().collect {
            Log.d("spirit", it.toString())
            _chapters.value = it
        }
    }

    fun getChapterDetail(chapter: String) = viewModelScope.launch {
        alexandriaRepo.getBloodyAgeChapter(chapter).collect {
            Log.d("spirit", it.toString())
            _chapterDetail.value = it
        }
    }
}