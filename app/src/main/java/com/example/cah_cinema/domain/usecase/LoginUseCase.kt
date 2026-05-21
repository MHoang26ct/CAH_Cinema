package com.example.cah_cinema.domain.usecase

import com.example.cah_cinema.domain.model.LoginResult
import com.example.cah_cinema.domain.repository.AuthRepository

class LoginUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<LoginResult> {
        return repository.login(email, password)
    }
}
