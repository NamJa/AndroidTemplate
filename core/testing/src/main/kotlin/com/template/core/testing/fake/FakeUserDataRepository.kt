package com.template.core.testing.fake

import com.template.core.domain.repository.UserDataRepository
import com.template.core.model.UserData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeUserDataRepository : UserDataRepository {

    private val _userData = MutableStateFlow(UserData(darkThemeEnabled = false))

    override val userData: Flow<UserData> = _userData.asStateFlow()

    override suspend fun setDarkThemeEnabled(enabled: Boolean) {
        _userData.value = _userData.value.copy(darkThemeEnabled = enabled)
    }
}
