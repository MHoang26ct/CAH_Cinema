package com.example.cah_cinema.domain.usecase

import com.example.cah_cinema.domain.model.LoginResult
import com.example.cah_cinema.domain.repository.AuthRepository

class RegisterUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String, name: String, phone: String?): Result<LoginResult> {
        return repository.register(email, password, name, phone)
    }
}
