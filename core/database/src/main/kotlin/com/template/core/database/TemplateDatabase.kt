package com.template.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.template.core.database.dao.ItemDao
import com.template.core.database.entity.ItemEntity

@Database(
    entities = [ItemEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class TemplateDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao
}
