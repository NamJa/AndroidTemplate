package com.template.core.domain.usecase

import com.template.core.domain.repository.ItemRepository
import com.template.core.model.Item
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetItemsUseCase @Inject constructor(
    private val repository: ItemRepository,
) {
    operator fun invoke(): Flow<List<Item>> = repository.getItems()
}
