package com.template.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.template.app.navigation.TemplateNavDisplay
import com.template.core.designsystem.theme.TemplateTheme

@Composable
fun TemplateApp(
    appViewModel: AppViewModel = hiltViewModel(),
) {
    val darkTheme by appViewModel.darkThemeEnabled.collectAsStateWithLifecycle()
    TemplateTheme(darkTheme = darkTheme) {
        TemplateNavDisplay()
    }
}
