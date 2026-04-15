package com.template.core.testing.fake

import com.template.core.domain.repository.ItemRepository
import com.template.core.model.Item
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeItemRepository : ItemRepository {

    private val itemsFlow = MutableStateFlow<List<Item>>(emptyList())

    var refreshCallCount: Int = 0
        private set

    fun setItems(items: List<Item>) {
        itemsFlow.value = items
    }

    override fun getItems(): Flow<List<Item>> = itemsFlow.asStateFlow()

    override suspend fun getItemById(id: Long): Item? = itemsFlow.value.firstOrNull { it.id == id }

    override suspend fun refresh() {
        refreshCallCount++
    }
}
