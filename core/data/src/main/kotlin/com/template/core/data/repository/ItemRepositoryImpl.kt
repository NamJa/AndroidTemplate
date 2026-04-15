package com.template.core.data.repository

import com.template.core.common.di.Dispatcher
import com.template.core.common.di.TemplateDispatchers
import com.template.core.data.mapper.toDomainModel
import com.template.core.data.mapper.toEntity
import com.template.core.database.dao.ItemDao
import com.template.core.domain.repository.ItemRepository
import com.template.core.model.Item
import com.template.core.network.datasource.ItemNetworkDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ItemRepositoryImpl @Inject constructor(
    private val itemDao: ItemDao,
    private val networkDataSource: ItemNetworkDataSource,
    @Dispatcher(TemplateDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : ItemRepository {

    override fun getItems(): Flow<List<Item>> = itemDao.observeAll()
        .map { entities -> entities.map { it.toDomainModel() } }
        .flowOn(ioDispatcher)

    override suspend fun getItemById(id: Long): Item? = withContext(ioDispatcher) {
        itemDao.getById(id)?.toDomainModel()
    }

    override suspend fun refresh() = withContext(ioDispatcher) {
        val response = networkDataSource.getItems()
        itemDao.upsertAll(response.map { it.toEntity() })
    }
}
