package com.template.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.template.core.domain.usecase.GetUserDataUseCase
import com.template.core.domain.usecase.SetDarkThemeEnabledUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    getUserDataUseCase: GetUserDataUseCase,
    private val setDarkThemeEnabledUseCase: SetDarkThemeEnabledUseCase,
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = getUserDataUseCase()
        .map<_, SettingsUiState> { SettingsUiState.Success(it.darkThemeEnabled) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SettingsUiState.Loading,
        )

    fun setDarkThemeEnabled(enabled: Boolean) {
        viewModelScope.launch {
            setDarkThemeEnabledUseCase(enabled)
        }
    }
}
