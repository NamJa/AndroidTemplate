package com.template.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.template.core.domain.usecase.GetUserDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    getUserDataUseCase: GetUserDataUseCase,
) : ViewModel() {

    val darkThemeEnabled: StateFlow<Boolean> = getUserDataUseCase()
        .map { it.darkThemeEnabled }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = false,
        )
}
