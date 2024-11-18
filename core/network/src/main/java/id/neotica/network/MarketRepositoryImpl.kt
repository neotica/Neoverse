package id.neotica.network

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.database.database
import id.neotica.domain.ApiResult
import id.neotica.domain.model.chat.ParentNeo
import id.neotica.domain.model.market.Hero
import id.neotica.domain.model.market.Item
import id.neotica.domain.remote.MarketRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class MarketRepositoryImpl: MarketRepository {
    private val firebase = Firebase.database

    override suspend fun getHeroes(): Flow<ApiResult<List<Hero>>> = flow {
        val list = mutableListOf<Hero>()
        list.clear()
        try {
            val reference = firebase.getReference("market").child("heroes").get().await()
            reference.children.forEach { data ->
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

    override suspend fun getHero(id: String): Flow<ApiResult<Hero>> = flow {
        val reference = firebase.getReference("market").child("heroes").child(id).get().await()
        try {
            val value = reference.getValue(Hero::class.java)
            emit(ApiResult.Success(value!!))
        } catch (e: NullPointerException) {
            emit(ApiResult.Error("exception: ${e.message}"))
        }
    }

    override suspend fun buyHero(hero: Hero, userId: String): ApiResult<String> {
        val reference = firebase.getReference("market").child("heroes").child(hero.id).removeValue()
        reference.await()

        val buyReference = firebase.getReference("userdata").child(userId)
            .child("world")
            .child("heroes")
            .child(hero.id).setValue(hero)
        buyReference.await()
        return if (buyReference.isSuccessful) {
            ApiResult.Success( "Is successful")
        } else ApiResult.Error("Error")
    }

    override suspend fun pawnHero(hero: Hero, userId: String): ApiResult<String> {
        val reference = firebase.getReference("userdata").child(userId)
            .child("world")
            .child("heroes")
            .child(hero.id).removeValue()
        reference.await()

        val buyReference = firebase.getReference("market")
            .child("heroes")
            .child(hero.id).setValue(hero)
        buyReference.await()
        return if (buyReference.isSuccessful) {
            ApiResult.Success( "Is successful")
        } else ApiResult.Error("Error")
    }

    override suspend fun getItems(): Flow<ApiResult<List<Item>>> = flow {
        val list = mutableListOf<Item>()
        list.clear()
        try {
            val reference = firebase.getReference("market").child("items").get().await()
            Log.d("repo item", reference.toString())
            reference.children.forEach { data ->
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

    override suspend fun getItem(id: String): Flow<ApiResult<Item>> = flow {
        val reference = firebase.getReference("market").child("items").child(id).get().await()
        try {
            val value = reference.getValue(Item::class.java)
            emit(ApiResult.Success(value!!))
        } catch (e: NullPointerException) {
            emit(ApiResult.Error("exception: ${e.message}"))
        }
    }

    override suspend fun buyItem(item: Item, userId: String): ApiResult<String> {
        val buyReference = firebase.getReference("userdata").child(userId)
            .child("world")
            .child("items")
            .child(item.id).setValue(item)
        buyReference.await()
        return if (buyReference.isSuccessful) {
            ApiResult.Success( "Is successful")
        } else ApiResult.Error("Error")
    }
}