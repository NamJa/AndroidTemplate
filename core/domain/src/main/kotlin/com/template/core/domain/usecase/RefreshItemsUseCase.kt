package com.template.core.domain.usecase

import com.template.core.domain.repository.ItemRepository
import javax.inject.Inject

class RefreshItemsUseCase @Inject constructor(
    private val repository: ItemRepository,
) {
    suspend operator fun invoke() = repository.refresh()
}
