package id.neotica.network

import com.google.firebase.Firebase
import com.google.firebase.database.database
import id.neotica.domain.ApiResult
import id.neotica.domain.model.Games
import id.neotica.domain.remote.GameRepository
import kotlinx.coroutines.tasks.await

class GameRepositoryImpl: GameRepository {
    private val firebase = Firebase.database

     override suspend fun getAuras(userId: String): ApiResult<Games?> {
         val ref = firebase.getReference("userdata")
             .child(userId).child("games").get().await()
         return try {
             val value = ref.getValue(Games::class.java)
             ApiResult.Success(value!!)
         } catch (e: Exception) {
             ApiResult.Error(e.message)
         }
     }

    override suspend fun postAuras(aura: Int, userId: String) {
        val original = firebase.getReference("userdata")
            .child(userId).child("games")
            .child("aura").setValue(aura)
        original.await()
    }
}