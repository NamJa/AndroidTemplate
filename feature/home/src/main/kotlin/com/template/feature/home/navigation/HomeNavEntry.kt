package com.template.feature.home.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.template.feature.home.HomeScreen

fun EntryProviderScope<NavKey>.homeEntry(
    onItemClick: (Long) -> Unit,
    onSettingsClick: () -> Unit,
) {
    entry<HomeRoute> {
        HomeScreen(
            onItemClick = onItemClick,
            onSettingsClick = onSettingsClick,
        )
    }
}
