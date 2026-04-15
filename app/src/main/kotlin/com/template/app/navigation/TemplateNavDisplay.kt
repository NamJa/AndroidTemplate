package com.template.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.template.feature.home.navigation.HomeRoute
import com.template.feature.home.navigation.homeEntry
import com.template.feature.settings.navigation.SettingsRoute
import com.template.feature.settings.navigation.settingsEntry

@Composable
fun TemplateNavDisplay() {
    val backStack = rememberNavBackStack(HomeRoute)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            homeEntry(
                onItemClick = { /* TODO: navigate to detail */ },
                onSettingsClick = { backStack.add(SettingsRoute) },
            )
            settingsEntry(
                onBackClick = { backStack.removeLastOrNull() },
            )
        },
    )
}
