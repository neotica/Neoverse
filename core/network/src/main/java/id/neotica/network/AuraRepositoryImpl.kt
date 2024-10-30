package id.neotica.network

import com.google.firebase.Firebase
import com.google.firebase.database.database
import id.neotica.domain.ApiResult
import id.neotica.domain.model.Games
import id.neotica.domain.remote.AuraRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class AuraRepositoryImpl: AuraRepository {
    private val firebase = Firebase.database

     override suspend fun getAuras(userId: String): ApiResult<Games?> {
         val ref = firebase.getReference("userdata")
             .child(userId).child("world").get().await()
         return try {
             val value = ref.getValue(Games::class.java)
             ApiResult.Success(value!!)
         } catch (e: Exception) {
             ApiResult.Error(e.message)
         }
     }

    override suspend fun getAurasFlow(userId: String): Flow<ApiResult<Games?>> = flow {
        val ref = firebase.getReference("userdata")
            .child(userId).child("world").get().await()
        try {
            val value = ref.getValue(Games::class.java)
            emit(ApiResult.Success(value!!))
        } catch (e: Exception) {
            emit(ApiResult.Error(e.message))
        }
    }

    override suspend fun getBankAura(userId: String): Flow<ApiResult<Games?>> = flow {
        val ref = firebase.getReference("bank").get().await()
        try {
            val value = ref.getValue(Games::class.java)
            emit(ApiResult.Success(value!!))
        } catch (e: Exception) {
            emit(ApiResult.Error(e.message))
        }
    }

    override suspend fun postAuras(aura: Int, userId: String) {
        val original = firebase.getReference("userdata")
            .child(userId).child("world")
            .child("aura").setValue(aura)
        original.await()
    }

    override suspend fun postAurasToBank(aura: Int, userId: String) {
        val original = firebase.getReference("bank").child("aura").setValue(aura)
        original.await()
    }
}