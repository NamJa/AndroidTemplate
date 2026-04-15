package com.template.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class ItemEntity(
    @PrimaryKey val id: Long,
    val title: String,
    val description: String,
    val imageUrl: String,
)
