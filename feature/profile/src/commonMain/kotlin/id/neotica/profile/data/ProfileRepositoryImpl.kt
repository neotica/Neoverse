package id.neotica.profile.data

import id.neotica.domain.ApiResult
import id.neotica.domain.NeoUser
import id.neotica.domain.model.User
import id.neotica.profile.domain.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ProfileRepositoryImpl : ProfileRepository {

    override suspend fun getUserDetail(user: String): ApiResult<User?> = ApiResult.Success(null)

    override suspend fun getUserDetailNew(user: String): Flow<ApiResult<NeoUser>> = flow {
        emit(ApiResult.Error("Profile editing is not available yet"))
    }

    override suspend fun fillProfile(user: NeoUser): Flow<ApiResult<NeoUser>> = flow {
        emit(ApiResult.Success(user))
    }

    override suspend fun getUserProfilePic(): String = DEFAULT_AVATAR

    override suspend fun changeProfilePicture(uri: String) = Unit

    override suspend fun changeUserProfile(user: User) = Unit

    private companion object {
        const val DEFAULT_AVATAR =
            "https://www.pngarts.com/files/6/User-Avatar-in-Suit-PNG.png"
    }
}
