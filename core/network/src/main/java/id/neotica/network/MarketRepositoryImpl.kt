package id.neotica.network

import android.util.Log
import androidx.core.net.toUri
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.google.firebase.storage.FirebaseStorage
import id.neotica.domain.ApiResult
import id.neotica.domain.model.chat.ParentNeo
import id.neotica.domain.model.market.Hero
import id.neotica.domain.remote.MarketRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

class MarketRepositoryImpl: MarketRepository {
    private val firebase = Firebase.database

    override suspend fun getItems(): Flow<ApiResult<List<Hero>>> = flow {
        val list = mutableListOf<Hero>()
        list.clear()
        try {
            val reference = firebase.getReference("market").child("heroes").get().await()
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

    override suspend fun getUserHeroes(userId: String): Flow<ApiResult<List<Hero>>> = flow {
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

    override suspend fun getHero(id: String): Flow<ApiResult<Hero>> = flow {
        val reference = firebase.getReference("market").child("heroes").child(id).get().await()
        try {
            val value = reference.getValue(Hero::class.java)
            emit(ApiResult.Success(value!!))
        } catch (e: NullPointerException) {
            emit(ApiResult.Error("exception: ${e.message}"))
        }
    }

    override suspend fun getUserHero(heroId: String, userId: String): Flow<ApiResult<Hero>> = flow {
        val reference = firebase.getReference("userdata").child(userId).child("world")
            .child("heroes").child(heroId).get().await()
        try {
            val value = reference.getValue(Hero::class.java)
            emit(ApiResult.Success(value!!))
        } catch (e: NullPointerException) {
            emit(ApiResult.Error("exception: ${e.message}"))
        }
    }

    override suspend fun addItem(hero: Hero): ApiResult<String> {
        val reference = firebase.getReference("market").child("heroes").child(hero.id).setValue(hero)
        reference.await()
        return if (reference.isSuccessful) {
            ApiResult.Success( "Is successful")
        } else ApiResult.Error("Error")
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

    override suspend fun uploadHeroPicture(heroId: String, uri: String): Flow<ApiResult<String>> = flow {
        emit(ApiResult.Loading())
        val storageRef = FirebaseStorage.getInstance().reference
        val upload = storageRef.child("heroPicture/${heroId}").putFile(uri.toUri())
        upload.await()
        if (upload.isSuccessful) {
            emit(ApiResult.Success("Success"))
        } else emit(ApiResult.Error("Welp."))
        if (upload.isCanceled) emit(ApiResult.Error("Canceled."))
    }

    @OptIn(InternalCoroutinesApi::class)
    override suspend fun getHeroPicture(id: String): String = withContext(Dispatchers.IO) {
        val storageRef = FirebaseStorage.getInstance().reference
            .child("heroPicture/${id}")

        suspendCancellableCoroutine { cont ->
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                cont.resume(uri.toString())
            }.addOnFailureListener { exc ->
                cont.tryResumeWithException(exc)
            }
        }
    }
}