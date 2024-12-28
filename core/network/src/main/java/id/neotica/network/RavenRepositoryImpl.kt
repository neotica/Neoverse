package id.neotica.network

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.database.database
import id.neotica.domain.ApiResult
import id.neotica.domain.model.raven.Raven
import id.neotica.domain.remote.RavenRepository
import id.neotica.domain.utils.randomUUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class RavenRepositoryImpl: RavenRepository {
    private val firebase = Firebase.database

    override suspend fun addWord(raven: Raven) {
        if (!raven.word.contains(Regex("[$.#]"))) {
            val reference = firebase.getReference("world").child("raven").child(raven.word)
                .child(randomUUID()).setValue(raven.value)
            reference.await()
        }
    }

    override suspend fun getWords(raven: Raven): Flow<ApiResult<List<String>>> = flow {
        val list = mutableListOf<String>()
        list.clear()
        emit(ApiResult.Loading())
        val reference = firebase.getReference("world").child("raven").child(raven.word.replace(Regex("[$.#]"), "")) .get().await()
        Log.d("raven", reference.toString())
        try {

            if (reference.value != null)
            reference.children.forEach { data ->
                val cr = data.value.toString()
                list.add(cr)
                emit(ApiResult.Success(list))
                Log.d("ravenchild", cr)
            } else {
                emit(ApiResult.Error("Sorry, I don't understand."))
            }
        } catch (e: NullPointerException) {
            emit(ApiResult.Error("exception: ${e.message}"))
        }
    }
}