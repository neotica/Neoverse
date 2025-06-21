package id.neotica.profile.domain

import id.neotica.domain.ApiResult
import id.neotica.domain.NeoUser
import id.neotica.domain.model.User
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    suspend fun getUserDetail(user: String): ApiResult<User?>
    suspend fun getUserDetailNew(user: String): Flow<ApiResult<NeoUser>>
    suspend fun fillProfile(user: NeoUser): Flow<ApiResult<NeoUser>>
    suspend fun getUserProfilePic(): String
    suspend fun changeProfilePicture(uri: String)
    suspend fun changeUserProfile(user: User)
}