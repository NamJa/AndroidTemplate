package com.template.feature.home

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.template.core.domain.usecase.GetItemsUseCase
import com.template.core.domain.usecase.RefreshItemsUseCase
import com.template.core.model.Item
import com.template.core.testing.fake.FakeItemRepository
import com.template.core.testing.rule.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeItemRepository()
    private val viewModel = HomeViewModel(
        getItemsUseCase = GetItemsUseCase(repository),
        refreshItemsUseCase = RefreshItemsUseCase(repository),
    )

    @Test
    fun `init triggers refresh`() = runTest {
        assertThat(repository.refreshCallCount).isEqualTo(1)
    }

    @Test
    fun `uiState emits Success with items from repository`() = runTest {
        val items = listOf(
            Item(id = 1L, title = "First", description = "desc", imageUrl = "https://example/1"),
        )
        repository.setItems(items)

        viewModel.uiState.test {
            assertThat(awaitItem()).isEqualTo(HomeUiState.Loading)
            assertThat(awaitItem()).isEqualTo(HomeUiState.Success(items = items))
        }
    }
}
