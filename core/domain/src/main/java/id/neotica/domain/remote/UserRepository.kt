package id.neotica.domain.remote

import android.net.Uri
import id.neotica.domain.ApiResult
import id.neotica.domain.model.User
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
    suspend fun changeProfilePicture(uri: Uri)
    suspend fun changeUserProfile(user: User)
    suspend fun getUserProfilePic(): Uri
}