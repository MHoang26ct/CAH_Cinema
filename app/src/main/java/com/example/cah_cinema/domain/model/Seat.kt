package com.example.cah_cinema.domain.model

enum class SeatType {
    REGULAR, VIP, COUPLE, AISLE
}

enum class SeatStatus {
    AVAILABLE, SELECTED, BOOKED, MAINTENANCE, TAKEN_BY_OTHERS
}

data class Seat(
    val id: String,
    val row: Double,       // tọa độ số gốc từ backend (1.0, 1.5, 2.0...)
    val col: Double,       // tọa độ số gốc từ backend
    val rowLabel: String?, // null = aisle ngang
    val colLabel: String?, // null = aisle dọc
    val type: SeatType,
    val status: SeatStatus
) {
    val isAisle: Boolean get() = type == SeatType.AISLE || rowLabel == null || colLabel == null
    val name: String get() = if (rowLabel != null && colLabel != null) "$rowLabel$colLabel" else ""
    // Backward compat
    val number: String get() = colLabel ?: ""
}
