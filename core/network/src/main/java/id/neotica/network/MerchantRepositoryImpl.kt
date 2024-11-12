package id.neotica.network

import androidx.core.net.toUri
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.google.firebase.storage.FirebaseStorage
import id.neotica.domain.ApiResult
import id.neotica.domain.model.market.Hero
import id.neotica.domain.remote.MerchantRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

class MerchantRepositoryImpl: MerchantRepository {
    private val firebase = Firebase.database


    override suspend fun addHero(hero: Hero): ApiResult<String> {
        val reference = firebase.getReference("market").child("heroes").child(hero.id).setValue(hero)
        reference.await()
        return if (reference.isSuccessful) {
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