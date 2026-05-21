package com.example.cah_cinema.domain.model

data class CinemaWithShowtimes(
    val cinemaId: Long,
    val cinemaName: String,
    val address: String,
    val showtimes: List<ShowtimeInfo>
)

data class ShowtimeInfo(
    val id: String,
    val time: String,
    val format: String
)
