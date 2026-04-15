package com.template.feature.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.template.core.designsystem.component.TemplateTopBar
import com.template.core.ui.ErrorState
import com.template.core.ui.ItemRow
import com.template.core.ui.LoadingState

@Composable
fun HomeScreen(
    onItemClick: (Long) -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    HomeContent(
        uiState = uiState,
        onItemClick = onItemClick,
        onSettingsClick = onSettingsClick,
        onRetry = viewModel::refresh,
    )
}

@Composable
internal fun HomeContent(
    uiState: HomeUiState,
    onItemClick: (Long) -> Unit,
    onSettingsClick: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TemplateTopBar(
                title = "Home",
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                        )
                    }
                },
            )
        },
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (uiState) {
                is HomeUiState.Loading -> LoadingState()
                is HomeUiState.Error -> ErrorState(message = uiState.message, onRetry = onRetry)
                is HomeUiState.Success -> LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(items = uiState.items, key = { it.id }) { item ->
                        ItemRow(item = item, onClick = onItemClick)
                    }
                }
            }
        }
    }
}
