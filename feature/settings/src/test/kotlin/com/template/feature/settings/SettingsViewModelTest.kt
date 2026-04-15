package com.template.feature.settings

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.template.core.domain.usecase.GetUserDataUseCase
import com.template.core.domain.usecase.SetDarkThemeEnabledUseCase
import com.template.core.testing.fake.FakeUserDataRepository
import com.template.core.testing.rule.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class SettingsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeUserDataRepository()
    private val viewModel = SettingsViewModel(
        getUserDataUseCase = GetUserDataUseCase(repository),
        setDarkThemeEnabledUseCase = SetDarkThemeEnabledUseCase(repository),
    )

    @Test
    fun `initial uiState is Loading`() = runTest {
        assertThat(viewModel.uiState.value).isEqualTo(SettingsUiState.Loading)
    }

    @Test
    fun `uiState reflects dark theme toggle`() = runTest {
        viewModel.uiState.test {
            assertThat(awaitItem()).isEqualTo(SettingsUiState.Loading)
            assertThat(awaitItem()).isEqualTo(SettingsUiState.Success(darkThemeEnabled = false))

            viewModel.setDarkThemeEnabled(true)
            assertThat(awaitItem()).isEqualTo(SettingsUiState.Success(darkThemeEnabled = true))
        }
    }
}
