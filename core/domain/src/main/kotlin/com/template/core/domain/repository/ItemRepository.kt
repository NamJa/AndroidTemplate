package com.template.core.domain.repository

import com.template.core.model.Item
import kotlinx.coroutines.flow.Flow

interface ItemRepository {
    fun getItems(): Flow<List<Item>>
    suspend fun getItemById(id: Long): Item?
    suspend fun refresh()
}
