package com.example.cah_cinema.domain.model

enum class SeatType {
    REGULAR, VIP, COUPLE
}

enum class SeatStatus {
    AVAILABLE, SELECTED, BOOKED, MAINTENANCE, TAKEN_BY_OTHERS
}

data class Seat(
    val id: String,
    val row: String,
    val number: String,
    val type: SeatType,
    val status: SeatStatus
) {
    val name: String get() = "$row$number"
}
