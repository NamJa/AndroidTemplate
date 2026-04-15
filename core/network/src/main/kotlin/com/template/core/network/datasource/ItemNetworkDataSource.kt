package com.template.core.network.datasource

import com.template.core.network.api.ItemApi
import com.template.core.network.model.ItemResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ItemNetworkDataSource @Inject constructor(
    private val api: ItemApi,
) {
    suspend fun getItems(): List<ItemResponse> = api.getItems()

    suspend fun getItem(id: Long): ItemResponse = api.getItem(id)
}
