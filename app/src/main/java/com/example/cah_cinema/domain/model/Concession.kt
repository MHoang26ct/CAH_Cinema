package com.example.cah_cinema.domain.model

enum class ConcessionType {
    DRINK, POPCORN
}

data class Concession(
    val id: String,
    val name: String,
    val price: Double,
    val imageUrl: String,
    val type: ConcessionType,
    val quantity: Int = 0
)
