package com.template.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.template.core.database.entity.ItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Query("SELECT * FROM items ORDER BY id ASC")
    fun observeAll(): Flow<List<ItemEntity>>

    @Query("SELECT * FROM items WHERE id = :id")
    suspend fun getById(id: Long): ItemEntity?

    @Upsert
    suspend fun upsertAll(items: List<ItemEntity>)

    @Query("DELETE FROM items")
    suspend fun deleteAll()
}
