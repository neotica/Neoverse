package id.neotica.domain.remote

import id.neotica.domain.ApiResult
import id.neotica.domain.model.alexandria.Book
import id.neotica.domain.model.alexandria.admin.WarningRequest
import id.neotica.domain.model.alexandria.admin.WarningResponse
import id.neotica.domain.pagination.Pagination
import kotlinx.coroutines.flow.Flow

interface AlexandriaAdminRepository {
    fun getBooks(): Flow<ApiResult<Pagination<Book>>>
    fun getWarnings(): Flow<ApiResult<Pagination<WarningResponse>>>
    fun updateWarning(id: String, warningRequest: WarningRequest): Flow<ApiResult<WarningResponse>>
}