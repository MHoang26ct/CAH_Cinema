package com.example.cah_cinema.domain.repository

import com.example.cah_cinema.domain.model.FeaturedMovies
import com.example.cah_cinema.domain.model.Movie

interface MovieRepository {
    suspend fun getFeaturedMovies(): Result<FeaturedMovies>
    suspend fun getMovies(): Result<List<Movie>>
    suspend fun getMovieDetail(id: Long): Result<Movie>
}
