package id.neotica.network

import com.google.firebase.Firebase
import com.google.firebase.database.database
import id.neotica.domain.ApiResult
import id.neotica.domain.model.World
import id.neotica.domain.remote.AuraRepository
import id.neotica.network.utils.LongCryptoUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class AuraRepositoryImpl: AuraRepository {
    private val firebase = Firebase.database

    override suspend fun getAuras(userId: String): Flow<ApiResult<World?>> = flow {
        val ref = firebase.getReference("userdata")
            .child(userId).child("world").get().await()
        try {
            val value = ref.getValue(World::class.java)
            emit(ApiResult.Success(value!!))
        } catch (e: Exception) {
            emit(ApiResult.Error(e.message))
        }
    }

    override suspend fun getAurasDecrypt(userId: String): Flow<ApiResult<Long?>> = flow {
        val ref = firebase.getReference("userdata")
            .child(userId).child("world").get().await()
        try {
            val aura = ref.child("aura").value
            val decryptAura = LongCryptoUtil.decrypt(aura.toString())
            emit(ApiResult.Success(decryptAura))
        } catch (e: Exception) {
            emit(ApiResult.Error(e.message))
        }
    }

    override suspend fun getBankAura(userId: String): Flow<ApiResult<World?>> = flow {
        val ref = firebase.getReference("bank").get().await()
        try {
            val value = ref.getValue(World::class.java)
            emit(ApiResult.Success(value!!))
        } catch (e: Exception) {
            emit(ApiResult.Error(e.message))
        }
    }

    override suspend fun postAuras(aura: Long, userId: String) {
        val auraEncrypted = LongCryptoUtil.encrypt(aura)
        val original = firebase.getReference("userdata")
            .child(userId).child("world")
            .child("aura").setValue(auraEncrypted)
        original.await()
    }

    override suspend fun postAurasToBank(aura: Long, userId: String) {
        val original = firebase.getReference("bank").child("aura").setValue(aura)
        original.await()
    }
}