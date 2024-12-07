package id.neotica.network

import com.google.firebase.Firebase
import com.google.firebase.database.database
import id.neotica.domain.ApiResult
import id.neotica.domain.remote.SuperAdminRepository
import id.neotica.network.utils.LongCryptoUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class SuperAdminRepositoryImpl: SuperAdminRepository {
    private val firebase = Firebase.database

    override suspend fun postAuras(aura: Long) {
        val auraEncrypted = LongCryptoUtil.encrypt(aura)
        val original = firebase.getReference("utils")
            .child("aura").setValue(auraEncrypted)
        original.await()
    }

    override suspend fun postMinimumVersion(version: Int) {
        val original = firebase.getReference("utils")
            .child("version").setValue(version)
        original.await()
    }

    override suspend fun getMinimumVersion(): Flow<ApiResult<Int>> = flow {
        val reference = firebase.getReference("utils").get().await()
        val value = reference.child("version").value
        try {
            emit(ApiResult.Success(value.toString().toInt()))
        } catch (e: Exception) {
            emit(ApiResult.Error(e.message.toString()))
        }
    }
}