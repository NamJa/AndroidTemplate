package com.template.core.domain.usecase

import com.template.core.domain.repository.UserDataRepository
import com.template.core.model.UserData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserDataUseCase @Inject constructor(
    private val repository: UserDataRepository,
) {
    operator fun invoke(): Flow<UserData> = repository.userData
}
