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

/**
 * Response cho GET /api/v1/public/showtimes/movies/{movieId}
 * Khớp với MovieShowtimesResponseDTO của backend — là một object, không phải List.
 */
data class MovieShowtimesResponse(
    @SerializedName("movie") val movie: MovieShowtimeMovieInfo?,
    @SerializedName("cinemas") val cinemas: List<MovieShowtimeItem>
)

data class MovieShowtimeMovieInfo(
    @SerializedName("movieId") val id: Long,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String?
)

data class MovieShowtimeItem(
    @SerializedName("cinemaId") val cinemaId: Long,
    @SerializedName("cinemaName") val cinemaName: String,
    @SerializedName("address") val address: String,
    @SerializedName("showtimes") val showtimes: List<ShowtimeInfo>
)

data class ShowtimeInfo(
    @SerializedName("showtimeId") val id: Long,
    @SerializedName("movieId") val movieId: Long? = null,
    @SerializedName("roomId") val roomId: Long? = null,
    @SerializedName("startTime") val startTime: String,
    @SerializedName("endTime") val endTime: String,
    @SerializedName("format") val format: String,
    @SerializedName("basePrice") val basePrice: Double,
    @SerializedName("status") val status: String,
    @SerializedName("roomName") val roomName: String
)
