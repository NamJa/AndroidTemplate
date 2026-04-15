package com.template.feature.settings

sealed interface SettingsUiState {
    data object Loading : SettingsUiState
    data class Success(val darkThemeEnabled: Boolean) : SettingsUiState
}
