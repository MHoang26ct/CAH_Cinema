package com.example.cah_cinema.domain.model

data class FeaturedMovies(
    val nowShowing: List<Movie>,
    val upcoming: List<Movie>
)
