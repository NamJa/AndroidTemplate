package com.template.core.data.repository

import com.template.core.datastore.UserPreferencesDataSource
import com.template.core.domain.repository.UserDataRepository
import com.template.core.model.UserData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class UserDataRepositoryImpl @Inject constructor(
    private val preferences: UserPreferencesDataSource,
) : UserDataRepository {

    override val userData: Flow<UserData> = preferences.darkThemeEnabled.map { darkTheme ->
        UserData(darkThemeEnabled = darkTheme)
    }

    override suspend fun setDarkThemeEnabled(enabled: Boolean) {
        preferences.setDarkThemeEnabled(enabled)
    }
}
