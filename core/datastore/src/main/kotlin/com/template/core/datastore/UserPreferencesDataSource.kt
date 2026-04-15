package com.template.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {

    val darkThemeEnabled: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[KEY_DARK_THEME] ?: false
    }

    suspend fun setDarkThemeEnabled(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[KEY_DARK_THEME] = enabled
        }
    }

    private companion object {
        val KEY_DARK_THEME = booleanPreferencesKey("dark_theme_enabled")
    }
}
