package com.example.cah_cinema.domain.model

data class Cinema(
    val id: Long,
    val name: String,
    val address: String,
    val imageUrl: String?,
    val hotline: String?
)
