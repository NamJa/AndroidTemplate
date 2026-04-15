package com.template.core.data.mapper

import com.template.core.database.entity.ItemEntity
import com.template.core.model.Item
import com.template.core.network.model.ItemResponse

internal fun ItemEntity.toDomainModel(): Item = Item(
    id = id,
    title = title,
    description = description,
    imageUrl = imageUrl,
)

internal fun ItemResponse.toEntity(): ItemEntity = ItemEntity(
    id = id,
    title = title,
    description = description,
    imageUrl = imageUrl,
)

internal fun ItemResponse.toDomainModel(): Item = Item(
    id = id,
    title = title,
    description = description,
    imageUrl = imageUrl,
)
