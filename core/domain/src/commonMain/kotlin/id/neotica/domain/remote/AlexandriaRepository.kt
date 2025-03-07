package id.neotica.domain.remote

import id.neotica.domain.ApiResult
import id.neotica.domain.model.alexandria.Spirits
import kotlinx.coroutines.flow.Flow

interface AlexandriaRepository {
    fun getSpirits(): Flow<ApiResult<List<Spirits>>>
}