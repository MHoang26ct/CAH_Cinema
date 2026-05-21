package com.example.cah_cinema.data.mapper

import com.example.cah_cinema.data.model.MovieDetail
import com.example.cah_cinema.data.model.MovieListItem
import com.example.cah_cinema.domain.model.Movie

fun MovieListItem.toDomainMovie(): Movie {
    return Movie(
        id = this.id.toString(),
        title = this.title,
        genre = "2D", // Backend doesn't provide genre in list, default to 2D
        posterUrl = this.posterUrl,
        age = this.ageRating,
        duration = "${this.duration} phút",
        format = "2D",
        releaseDate = this.releaseDate
    )
}

fun MovieDetail.toDomainMovie(): Movie {
    return Movie(
        id = this.id.toString(),
        title = this.title,
        genre = this.genres.joinToString(", ") { it.name },
        posterUrl = this.posterUrl,
        age = this.ageRating,
        duration = "${this.duration} phút",
        format = "2D",
        description = this.description,
        director = this.directorName ?: "",
        cast = this.actorList ?: "",
        releaseDate = this.releaseDate,
        bannerUrl = this.posterUrl // Use poster as banner if not provided separately
    )
}
