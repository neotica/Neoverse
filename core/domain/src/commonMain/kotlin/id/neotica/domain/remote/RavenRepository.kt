package id.neotica.domain.remote

import id.neotica.domain.ApiResult
import id.neotica.domain.model.chat.ParentNeo
import id.neotica.domain.model.raven.Raven
import id.neotica.domain.model.raven.RavenWords
import kotlinx.coroutines.flow.Flow

interface RavenRepository {
    suspend fun addWord(raven: Raven)
    suspend fun getWords(raven: Raven): Flow<ApiResult<List<ParentNeo<RavenWords>>>>
}