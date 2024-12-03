package id.neotica.network

import com.google.firebase.Firebase
import com.google.firebase.database.database
import id.neotica.domain.ApiResult
import id.neotica.domain.model.UserData
import id.neotica.domain.model.UserDataFirebase
import id.neotica.domain.model.chat.ParentNeo
import id.neotica.domain.remote.FeedRepository
import id.neotica.network.utils.userDataToLong
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class FeedRepositoryImpl: FeedRepository {
    private val firebase = Firebase.database

    override fun getAllUserData(): Flow<ApiResult<List<ParentNeo<UserData>>>> = flow {
        val list = mutableListOf<ParentNeo<UserData>>()
        list.clear()
        emit(ApiResult.Loading())
        val ref = firebase.getReference("userdata").get().await()

        try {
            ref.children.forEach {
                val value = it.getValue(UserDataFirebase::class.java)
                val parentValue = ParentNeo(
                    key = it.key!!,
                    value = value!!
                )
                val aura = it.child("world").child("aura").value
                if (aura != null && aura.toString() != "null" && aura.toString() != "") {
                    val userDataLong = userDataToLong(parentValue)
                    list.add(userDataLong)
                }
            }

            emit(ApiResult.Success(
                list.filter {
                    it.value.world.username!= "" &&
                            it.value.world.username!= "laetuz"
                }
                    .sortedByDescending { it.value.world.aura }
            ))
        } catch (e: Exception) {
            emit(ApiResult.Error(e.message))
        }
    }

    override suspend fun inputUsernameToWorld(username: String, userId: String) {
        val reference = firebase.reference.child("userdata").child(userId)
            .child("world").child("username").setValue(username)
        reference.await()
    }
}