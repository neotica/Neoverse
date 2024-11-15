package id.neotica.network

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.database.database
import id.neotica.domain.ApiResult
import id.neotica.domain.model.chat.ParentNeo
import id.neotica.domain.model.market.Hero
import id.neotica.domain.model.market.Item
import id.neotica.domain.remote.InventoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class InventoryRepositoryImpl: InventoryRepository {
    private val firebase = Firebase.database

    override suspend fun getHeroes(userId: String): Flow<ApiResult<List<Hero>>> = flow {
        val list = mutableListOf<Hero>()
        list.clear()
        try {
            val reference = firebase.getReference("userdata").child(userId)
                .child("world").child("heroes").get().await()
            reference.children.forEach { data ->
                Log.d("marketrepo", data.toString())
                val cr = data.getValue(Hero::class.java)
                val channel = ParentNeo(
                    key = data.key!!,
                    value = cr!!
                )
                list.add(channel.value)
                if (data.value != null) {
                    emit(ApiResult.Success(list.sortedByDescending { it.createdAt }))
                } else {
                    emit(ApiResult.Error("data null"))
                }
            }
        } catch (e: NullPointerException) {
            emit(ApiResult.Error("exception: ${e.message}"))
        }
    }

    override suspend fun getItems(userId: String): Flow<ApiResult<List<Item>>> = flow {
        val list = mutableListOf<Item>()
        list.clear()
        try {
            val reference = firebase.getReference("userdata").child(userId)
                .child("world").child("items").get().await()
            reference.children.forEach { data ->
                Log.d("marketrepo", data.toString())
                val cr = data.getValue(Item::class.java)
                val channel = ParentNeo(
                    key = data.key!!,
                    value = cr!!
                )
                list.add(channel.value)
                if (data.value != null) {
                    emit(ApiResult.Success(list.sortedByDescending { it.createdAt }))
                } else {
                    emit(ApiResult.Error("data null"))
                }
            }
        } catch (e: NullPointerException) {
            emit(ApiResult.Error("exception: ${e.message}"))
        }
    }
}