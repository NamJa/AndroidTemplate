package com.template.feature.settings.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.template.feature.settings.SettingsScreen

fun EntryProviderScope<NavKey>.settingsEntry(
    onBackClick: () -> Unit,
) {
    entry<SettingsRoute> {
        SettingsScreen(onBackClick = onBackClick)
    }
}
