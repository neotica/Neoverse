package id.neotica.network

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.database.database
import id.neotica.domain.ApiResult
import id.neotica.domain.model.chat.ParentNeo
import id.neotica.domain.model.market.Hero
import id.neotica.domain.remote.MarketRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class MarketRepositoryImpl: MarketRepository {
    private val firebase = Firebase.database

    override suspend fun getItems(): Flow<ApiResult<List<Hero>>> = flow {
        val list = mutableListOf<Hero>()
        list.clear()
        try {
            val reference = firebase.getReference("market").get().await()
            reference.children.forEach { data ->
                Log.d("marketrepo", data.toString())
                val cr = data.getValue(Hero::class.java)
                val channel = ParentNeo(
                    key = data.key!!,
                    value = cr!!
                )
                list.add(channel.value)
                if (data.value != null) {
                    emit(ApiResult.Success(list))
                } else {
                    emit(ApiResult.Error("data null"))
                }
            }
        } catch (e: NullPointerException) {
            emit(ApiResult.Error("exception: ${e.message}"))
        }
    }

    override suspend fun getItem(id: String): ApiResult<Hero> {
        val reference = firebase.getReference("market").child(id).get().await()
        return try {
            val value = reference.getValue(Hero::class.java)
            ApiResult.Success(value!!)
        } catch (e: NullPointerException) {
            ApiResult.Error("exception: ${e.message}")
        }
    }

    override suspend fun addItem(hero: Hero) {
        val reference = firebase.getReference("market").child(hero.id).setValue(hero)
        reference.await()
    }

    suspend fun deleteItem() {}
}