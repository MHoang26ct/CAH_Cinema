package com.example.cah_cinema.domain.model

data class Promotion(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val conditions: List<String> = emptyList(),
    val notes: List<String> = emptyList()
)
