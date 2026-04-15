package com.template.core.domain.repository

import com.template.core.model.UserData
import kotlinx.coroutines.flow.Flow

interface UserDataRepository {
    val userData: Flow<UserData>
    suspend fun setDarkThemeEnabled(enabled: Boolean)
}
