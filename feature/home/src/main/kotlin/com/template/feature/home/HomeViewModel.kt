package com.template.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.template.core.domain.usecase.GetItemsUseCase
import com.template.core.domain.usecase.RefreshItemsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    getItemsUseCase: GetItemsUseCase,
    private val refreshItemsUseCase: RefreshItemsUseCase,
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = getItemsUseCase()
        .map<List<com.template.core.model.Item>, HomeUiState>(HomeUiState::Success)
        .catch { emit(HomeUiState.Error(it.message.orEmpty())) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState.Loading,
        )

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            runCatching { refreshItemsUseCase() }
        }
    }
}
