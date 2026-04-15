package com.template.core.domain.usecase

import com.template.core.domain.repository.UserDataRepository
import javax.inject.Inject

class SetDarkThemeEnabledUseCase @Inject constructor(
    private val repository: UserDataRepository,
) {
    suspend operator fun invoke(enabled: Boolean) {
        repository.setDarkThemeEnabled(enabled)
    }
}
