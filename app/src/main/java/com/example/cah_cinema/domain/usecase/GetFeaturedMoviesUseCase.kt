package com.example.cah_cinema.domain.usecase

import com.example.cah_cinema.domain.model.FeaturedMovies
import com.example.cah_cinema.domain.repository.MovieRepository

class GetFeaturedMoviesUseCase(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(): Result<FeaturedMovies> {
        return repository.getFeaturedMovies()
    }
}
