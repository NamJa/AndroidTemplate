package com.template.core.data.di

import com.template.core.data.repository.ItemRepositoryImpl
import com.template.core.data.repository.UserDataRepositoryImpl
import com.template.core.domain.repository.ItemRepository
import com.template.core.domain.repository.UserDataRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindsItemRepository(impl: ItemRepositoryImpl): ItemRepository

    @Binds
    @Singleton
    abstract fun bindsUserDataRepository(impl: UserDataRepositoryImpl): UserDataRepository
}
