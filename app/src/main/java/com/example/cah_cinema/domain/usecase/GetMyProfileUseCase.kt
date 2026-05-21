package com.example.cah_cinema.domain.usecase

import com.example.cah_cinema.domain.model.User
import com.example.cah_cinema.domain.repository.UserRepository

class GetMyProfileUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(): Result<User> {
        return repository.getMyProfile()
    }
}
