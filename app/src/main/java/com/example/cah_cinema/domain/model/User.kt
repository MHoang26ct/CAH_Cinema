package com.example.cah_cinema.domain.model

data class User(
    val id: Long,
    val name: String,
    val email: String,
    val phone: String?,
    val avatarUrl: String?,
    val role: String,
    val totalPaid: Double,
    val totalPoint: Int,
    val rankLevel: String
)
