package id.neotica.network

import com.google.firebase.Firebase
import com.google.firebase.database.database
import id.neotica.domain.model.market.ItemModel
import kotlinx.coroutines.tasks.await
import java.util.UUID

class MarketRepositoryImpl {
    private val firebase = Firebase.database

    suspend fun addItem(itemModel: ItemModel) {
        val key = UUID.randomUUID()
            //firebase.getReference("market").push().key
        val reference = firebase.getReference("market").child("item"+key!!).setValue(itemModel)
        reference.await()
    }

    suspend fun deleteItem() {}
}