package id.neotica.network

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.database.database
import id.neotica.domain.ApiResult
import id.neotica.domain.model.UserData
import id.neotica.domain.model.UserDataFirebase
import id.neotica.domain.model.chat.ParentNeo
import id.neotica.domain.model.tavern.TavernPost
import id.neotica.domain.remote.FeedRepository
import id.neotica.network.utils.userDataToLong
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class FeedRepositoryImpl: FeedRepository {
    private val firebase = Firebase.database

    override fun getAllUserData(): Flow<ApiResult<List<ParentNeo<UserData>>>> = flow {
        emit(ApiResult.Loading())
        val ref = firebase.getReference("userdata").get().await()
        try {
            val result = ref.children.mapNotNull { dataSnapshot ->
                val value = dataSnapshot.getValue(UserDataFirebase::class.java)
                val aura = dataSnapshot.child("world").child("aura").value?.toString()
                if (value != null && aura != null && aura != "null" && aura.isNotEmpty()) {
                    val parentValue = ParentNeo(key = dataSnapshot.key!!, value = value)
                    userDataToLong(parentValue)
                } else null
            }.filter {
                it.value.world.username.isNotEmpty() && it.value.world.username != "laetuz"
            }.sortedByDescending {
                it.value.world.aura
            }
            emit(ApiResult.Success(result))
        } catch (e: Exception) {
            emit(ApiResult.Error(e.message))
        }
    }

    override suspend fun inputUsernameToWorld(username: String, userId: String) {
        val reference = firebase.reference.child("userdata").child(userId)
            .child("world").child("username").setValue(username)
        reference.await()
    }

    override suspend fun postWhisper(post: TavernPost) {
        val reference = firebase.reference.child("world").child("tavern")
            .child("whispers").child(post.id).setValue(post)
        reference.await()
    }

    override suspend fun getWhispers(): Flow<ApiResult<List<ParentNeo<TavernPost>>>> = flow {
        val list = mutableListOf<ParentNeo<TavernPost>>()
        list.clear()
        emit(ApiResult.Loading())
        val ref = firebase.getReference("world").child("tavern").child("whispers").get().await()
        Log.d("feedRepo1", ref.toString())
        try {
            ref.children.forEach {
                val value = it.getValue(TavernPost::class.java)
                val parentValue = ParentNeo(
                    key = it.key!!,
                    value = value!!
                )
                list.add(parentValue)
                Log.d("feedRepo2", value.toString())
            }

            emit(ApiResult.Success(list.sortedByDescending { it.value.createdAt }))
        } catch (e: Exception) {
            emit(ApiResult.Error(e.message))
        }
    }

    override suspend fun postLogs(post: TavernPost) {
        val reference = firebase.reference.child("world").child("tavern")
            .child("logs").child(post.id).setValue(post)
        reference.await()
    }

    override suspend fun getLogs(): Flow<ApiResult<List<ParentNeo<TavernPost>>>> = flow {
        val list = mutableListOf<ParentNeo<TavernPost>>()
        list.clear()
        emit(ApiResult.Loading())
        val ref = firebase.getReference("world").child("tavern").child("logs").get().await()
        Log.d("feedRepo3", ref.toString())
        try {
            ref.children.forEach {
                val value = it.getValue(TavernPost::class.java)
                val parentValue = ParentNeo(
                    key = it.key!!,
                    value = value!!
                )
                list.add(parentValue)
                Log.d("feedRepo4", value.toString())
            }

            emit(ApiResult.Success(list.sortedByDescending { it.value.createdAt }))
        } catch (e: Exception) {
            emit(ApiResult.Error(e.message))
        }
    }

}