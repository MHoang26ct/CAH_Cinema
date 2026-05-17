package com.example.cah_cinema.data.model

import com.google.gson.annotations.SerializedName

data class MoviePageData(
    @SerializedName("content") val content: List<MovieListItem>,
    @SerializedName("totalElements") val totalElements: Long,
    @SerializedName("totalPages") val totalPages: Int
)

data class FeaturedMoviesData(
    @SerializedName("nowShowing") val nowShowing: List<MovieListItem>,
    @SerializedName("upcoming") val upcoming: List<MovieListItem>
)

data class MovieListItem(
    @SerializedName("movieId") val id: Long,
    @SerializedName("title") val title: String,
    @SerializedName("duration") val duration: Int,
    @SerializedName("ageRating") val ageRating: String,
    @SerializedName("posterUrl") val posterUrl: String
)

data class MovieDetail(
    @SerializedName("movieId") val id: Long,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("duration") val duration: Int,
    @SerializedName("releaseDate") val releaseDate: String,
    @SerializedName("ageRating") val ageRating: String,
    @SerializedName("posterUrl") val posterUrl: String,
    @SerializedName("trailerUrl") val trailerUrl: String?,
    @SerializedName("directorName") val directorName: String?,
    @SerializedName("actorList") val actorList: String?,
    @SerializedName("genres") val genres: List<Genre>
)

data class Genre(
    @SerializedName("genreId") val id: Long,
    @SerializedName("name") val name: String
)

data class PreLockRequest(
    @SerializedName("showtimeId") val showtimeId: Long,
    @SerializedName("seatIds") val seatIds: List<Long>
)
