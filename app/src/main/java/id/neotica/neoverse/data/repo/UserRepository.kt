package id.neotica.neoverse.data.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import id.neotica.neoverse.data.model.User

class UserRepository {
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    fun isLoggedIn(): Boolean {
        auth = FirebaseAuth.getInstance()
        return auth.currentUser != null
    }

    fun userId(): String {
        return if (isLoggedIn()) auth.currentUser!!.uid else ""
    }

    fun getUsers(): LiveData<List<User>> {

        val users = MutableLiveData<List<User>>()

        firestore.collection("Users").addSnapshotListener { snapshot, exception ->

            if (exception != null) {

                return@addSnapshotListener
            }

            val usersList = mutableListOf<User>()
            snapshot?.documents?.forEach { document ->

                val user = document.toObject(User::class.java)

                if (user!!.userId != userId()) {
                    user.let {
                        usersList.add(it)
                    }
                }
                users.value = usersList
            }
        }
        return users
    }

    /*suspend fun getUsers(): Flow<ApiResult<List<User>>> = flow {
        emit(ApiResult.Loading())
        val userList = listOf<User>()

        val snapshotListener = firestore.collection("Users")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) throw exception

                snapshot?.documents?.forEach { document ->
                    val user = document.toObject(User::class.java)

                    if (user != null && user.userId != userId()) {
                        userList
                    }
                }

                emit(ApiResult.Success(userList))
            }

    }.flowOn(Dispatchers.IO)*/
}