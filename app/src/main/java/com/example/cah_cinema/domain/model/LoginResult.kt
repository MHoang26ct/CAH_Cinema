package com.example.cah_cinema.domain.model

data class LoginResult(
    val accessToken: String,
    val refreshToken: String,
    val user: User
)
