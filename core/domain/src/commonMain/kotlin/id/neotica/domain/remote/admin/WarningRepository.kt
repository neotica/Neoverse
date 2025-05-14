package id.neotica.domain.remote.admin

import id.neotica.domain.ApiResult
import id.neotica.domain.model.admin.WarningRequest
import id.neotica.domain.model.admin.WarningResponse
import id.neotica.domain.pagination.Pagination
import kotlinx.coroutines.flow.Flow

interface WarningRepository {
    fun getWarnings(): Flow<ApiResult<Pagination<WarningResponse>>>
    fun newWarning(bookId: String, request: WarningRequest): Flow<ApiResult<WarningResponse>>
    fun updateWarning(bookId: String, request: WarningRequest): Flow<ApiResult<WarningResponse>>
    fun deleteWarning(id: String): Flow<ApiResult<String>>
}