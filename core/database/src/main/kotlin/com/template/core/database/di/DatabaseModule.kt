package com.template.core.database.di

import android.content.Context
import androidx.room.Room
import com.template.core.database.TemplateDatabase
import com.template.core.database.dao.ItemDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {

    @Provides
    @Singleton
    fun providesTemplateDatabase(
        @ApplicationContext context: Context,
    ): TemplateDatabase = Room
        .databaseBuilder(
            context = context,
            klass = TemplateDatabase::class.java,
            name = "template-database",
        )
        .build()

    @Provides
    fun providesItemDao(database: TemplateDatabase): ItemDao = database.itemDao()
}
