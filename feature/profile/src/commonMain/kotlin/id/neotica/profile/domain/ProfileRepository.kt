package id.neotica.profile.domain

import id.neotica.domain.ApiResult
import id.neotica.domain.model.User

interface ProfileRepository {
    suspend fun getUserDetail(user: String): ApiResult<User?>
    suspend fun getUserProfilePic(): String
    suspend fun changeProfilePicture(uri: String)
    suspend fun changeUserProfile(user: User)
}