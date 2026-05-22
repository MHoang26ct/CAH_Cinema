package com.example.cah_cinema.domain.model

data class Movie(
    val id: String,
    val title: String,
    val genre: String,
    val posterUrl: String,
    val age: String = "P",
    val format: String = "2D",
    val isFeatured: Boolean = false,
    val releaseDate: String = "",
    val duration: String = "",
    val director: String = "",
    val cast: String = "",
    val description: String = "",
    val bannerUrl: String = "",
    val isUpcoming: Boolean = false,
    val trailerUrl: String? = null
)

data class Showtime(
    val id: String,
    val time: String,
    val format: String = "2D"
)

data class Cinema(
    val id: String,
    val name: String,
    val address: String,
    val imageUrl: String = "",
    val phone: String = "",
    val showtimes: List<Showtime> = emptyList()
)

data class MovieDate(
    val dayOfWeek: String,
    val date: String,
    val isSelected: Boolean = false
)
