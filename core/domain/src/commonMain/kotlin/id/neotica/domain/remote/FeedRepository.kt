package id.neotica.domain.remote

import id.neotica.domain.ApiResult
import id.neotica.domain.model.UserData
import id.neotica.domain.model.chat.ParentNeo
import id.neotica.domain.model.tavern.TavernPost
import kotlinx.coroutines.flow.Flow

interface FeedRepository {
    fun getAllUserData(): Flow<ApiResult<List<ParentNeo<UserData>>>>
    suspend fun inputUsernameToWorld(username: String, userId: String)
    suspend fun postWhisper(post: TavernPost)
    suspend fun getWhispers(): Flow<ApiResult<List<ParentNeo<TavernPost>>>>
}