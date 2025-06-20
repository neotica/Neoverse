package id.neotica.profile.data

import dev.gitlive.firebase.firestore.FirebaseFirestore
import id.neotica.auth.domain.AuthRepository
import id.neotica.domain.ApiResult
import id.neotica.domain.NeoUser
import id.neotica.domain.model.User
import id.neotica.ktor.utils.safeApiCall
import id.neotica.neoverse.utils.baseUrl
import id.neotica.profile.data.local.ProfileDao
import id.neotica.profile.domain.ProfileRepository
import id.neotica.profile.utils.toEntity
import id.neotica.profile.utils.toNeoUser
import id.neotica.profile.utils.toUser
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.coroutines.flow.Flow

class ProfileRepositoryImpl(
    val firestore: FirebaseFirestore,
    private val authRepo: AuthRepository,
    private val profileDao: ProfileDao,
    private val client: HttpClient
): ProfileRepository {

    override suspend fun getUserDetail(user: String): ApiResult<User?> {
        return try {
            val snapshot = firestore.collection("Users").document(user).get()
            if (snapshot.exists) {
                val userValue = snapshot.data<User>()
                // Save user data in Firestore
                val post = client.post("$baseUrl/user") {
                    headers.append("Content-Type", "application/json")
                    setBody(
                        userValue.toNeoUser()
                    )
                }
                post
                ApiResult.Success(userValue)
            } else {
                ApiResult.Success(null)
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message)
        }
    }

    override suspend fun getUserDetailNew(user: String): Flow<ApiResult<NeoUser>> = safeApiCall{
        val profile = profileDao.getProfile(user)
        if (profile != null) {
            profile.toUser()
        } else {
            val client = client.get("$baseUrl/user/firebase/$user").body<NeoUser>()
            profileDao.insertProfile(client.toEntity())
            client
        }
    }

    override suspend fun getUserProfilePic(): String {
        TODO("Not yet implemented")
    }
//    @OptIn(InternalCoroutinesApi::class)
//    override suspend fun getUserProfilePic(): String = withContext(Dispatchers.IO) {
//        val storageRef = FirebaseStorage.getInstance().reference
//            .child("profilePicture/${getUserId()}")
//
//        suspendCancellableCoroutine { cont ->
//            storageRef.downloadUrl.addOnSuccessListener { uri ->
//                cont.resume(uri.toString())
//            }.addOnFailureListener { exc ->
//                cont.tryResumeWithException(exc)
//            }
//        }
//    }

    override suspend fun changeProfilePicture(uri: String) {
        TODO("Not yet implemented")
    }
//    override suspend fun changeProfilePicture(uri: String) {
//        val storageRef = FirebaseStorage.getInstance().reference
//        storageRef.child("profilePicture/${getUserId()}").putFile(uri.toUri())
//    }

    override suspend fun changeUserProfile(user: User) {
        firestore.collection("Users").document(authRepo.getUserId().toString()).set(user)
    }
}