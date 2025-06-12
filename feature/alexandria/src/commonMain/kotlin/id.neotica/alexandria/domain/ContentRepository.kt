package id.neotica.alexandria.domain

import id.neotica.domain.ApiResult
import id.neotica.domain.model.alexandria.ChapterContent
import id.neotica.domain.model.alexandria.ContentRequest
import id.neotica.domain.model.alexandria.content.NewContentRequest
import id.neotica.domain.pagination.PaginatedContent
import kotlinx.coroutines.flow.Flow

interface ContentRepository {
    fun getContents(chapterId: Int, page: Int? = null): Flow<ApiResult<PaginatedContent>>
    fun createContent(content: ContentRequest): Flow<ApiResult<ChapterContent>>
    fun editContent(content: NewContentRequest, id: Int, page: Int): Flow<ApiResult<String>>
    fun deleteContent(contentId: Int): Flow<ApiResult<String>>
}