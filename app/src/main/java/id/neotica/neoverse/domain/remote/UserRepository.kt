package id.neotica.neoverse.domain.remote

import id.neotica.neoverse.data.remote.model.User
import id.neotica.neoverse.data.remote.ApiResult
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun isLoggedIn(): Boolean
    fun getUserId(): String
    suspend fun getUserDetail(user: String): ApiResult<User?>
    suspend fun getAllUsers(): ApiResult<List<User>>
    fun getFriends(): Flow<ApiResult<List<User>>>
    suspend fun searchUser(search: String): ApiResult<List<User>>
    suspend fun addFriend(friend: User)
    fun isUserVerified(): Boolean
    suspend fun unfriend(friend: User)
}