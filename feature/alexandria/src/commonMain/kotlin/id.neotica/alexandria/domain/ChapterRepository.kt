package id.neotica.alexandria.domain

import id.neotica.domain.ApiResult
import id.neotica.domain.model.alexandria.Chapter
import id.neotica.domain.model.alexandria.chapter.ChapterRequest
import kotlinx.coroutines.flow.Flow

interface ChapterRepository {
    fun getChapters(bookId: String): Flow<ApiResult<List<Chapter>>>
    fun createChapter(chapter: ChapterRequest): Flow<ApiResult<Chapter>>
    fun deleteChapter(chapterId: Int): Flow<ApiResult<String>>
}