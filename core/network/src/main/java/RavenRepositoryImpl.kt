import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.database.database
import id.neotica.domain.ApiResult
import id.neotica.domain.model.chat.ParentNeo
import id.neotica.domain.model.raven.Raven
import id.neotica.domain.model.raven.RavenWords
import id.neotica.domain.remote.RavenRepository
import id.neotica.domain.utils.randomUUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class RavenRepositoryImpl: RavenRepository {
    private val firebase = Firebase.database

    override suspend fun addWord(raven: Raven) {
        val reference = firebase.getReference("world").child("raven").child(raven.word)
            .child(randomUUID()).setValue(raven.value)
        reference.await()
    }

    override suspend fun getWords(raven: Raven): Flow<ApiResult<List<ParentNeo<RavenWords>>>> = flow {
        val list = mutableListOf<ParentNeo<RavenWords>>()
        list.clear()
        emit(ApiResult.Loading())
        val reference = firebase.getReference("world").child("raven").child(raven.word).get().await()
        Log.d("raven", reference.toString())
        try {

            Log.d("raven", reference.toString())
//            reference.children.forEach { data ->
//                val cr = data.getValue(RavenWords::class.java)
//
//                val channel = ParentNeo(
//                    key = data.key!!,
//                    value = crDecrypted!!
//                )
//                list.add(channel)
//                if (data.value != null) {
//                    emit(
//                        ApiResult.Success(
//                        list.sortedByDescending { it.value.lastOpened })
//                    )
//                } else {
//                    emit(ApiResult.Error("data null"))
//                }
//            }
        } catch (e: NullPointerException) {
            emit(ApiResult.Error("exception: ${e.message}"))
        }
    }
}