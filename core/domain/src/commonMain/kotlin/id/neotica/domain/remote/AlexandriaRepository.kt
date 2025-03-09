package id.neotica.domain.remote

import id.neotica.domain.ApiResult
import id.neotica.domain.model.alexandria.BloodyAgeChapters
import id.neotica.domain.model.alexandria.ChapterDetail
import id.neotica.domain.model.alexandria.Spirits
import kotlinx.coroutines.flow.Flow

interface AlexandriaRepository {
    fun getSpirits(): Flow<ApiResult<List<Spirits>>>
    fun getBloodyAge(): Flow<ApiResult<List<BloodyAgeChapters>>>
    fun getBloodyAgeChapter(chapter: String): Flow<ApiResult<List<ChapterDetail>>>
}