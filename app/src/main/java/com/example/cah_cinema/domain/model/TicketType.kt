package com.example.cah_cinema.domain.model

data class TicketType(
    val id: String,
    val name: String,
    val price: Double,
    val quantity: Int = 0
)
