package id.neotica.domain.remote

import id.neotica.domain.ApiResult
import id.neotica.domain.model.raven.Raven
import kotlinx.coroutines.flow.Flow

interface RavenRepository {
    suspend fun addWord(raven: Raven)
    suspend fun getWords(raven: Raven): Flow<ApiResult<List<String>>>
}