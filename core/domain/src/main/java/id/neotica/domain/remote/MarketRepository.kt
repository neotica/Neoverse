package id.neotica.domain.remote

import id.neotica.domain.ApiResult
import id.neotica.domain.model.market.ItemModel
import kotlinx.coroutines.flow.Flow

interface MarketRepository {
    suspend fun addItem(itemModel: ItemModel)
    suspend fun getItems(): Flow<ApiResult<List<ItemModel>>>
}