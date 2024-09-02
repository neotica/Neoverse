package id.neotica.neoverse.domain

import id.neotica.neoverse.data.model.Friends
import id.neotica.neoverse.data.model.User
import id.neotica.neoverse.data.remote.ApiResult
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun isLoggedIn(): Boolean
    fun getUserId(): String
    suspend fun getUserDetail(user: String): ApiResult<User?>
    suspend fun getAllUsers(): ApiResult<List<User>>
    fun getFriends(): Flow<ApiResult<List<Friends>>>
    suspend fun searchUser(search: String): ApiResult<List<User>>
    suspend fun addFriend(friend: Friends)
}