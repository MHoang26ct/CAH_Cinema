package com.example.cah_cinema.data.model

import com.google.gson.annotations.SerializedName

data class CinemaItem(
    @SerializedName("cinemaId") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("address") val address: String,
    @SerializedName("imageUrl") val imageUrl: String? = null,
    @SerializedName("hotline") val hotline: String
)

data class RoomItem(
    @SerializedName("roomId") val id: Long,
    @SerializedName("cinemaId") val cinemaId: Long,
    @SerializedName("roomName") val name: String
)

data class CinemaShowtimeItem(
    @SerializedName("movie") val movie: MovieInfo,
    @SerializedName("showtimes") val showtimes: List<ShowtimeInfo>
)

data class MovieShowtimeItem(
    @SerializedName("cinemaId") val cinemaId: Long,
    @SerializedName("cinemaName") val cinemaName: String,
    @SerializedName("address") val address: String,
    @SerializedName("showtimes") val showtimes: List<ShowtimeInfo>
)

data class MovieShowtimesResponse(
    @SerializedName("movie") val movie: MovieInfoInShowtime,
    @SerializedName("cinemas") val cinemas: List<MovieShowtimeItem>
)

data class MovieInfoInShowtime(
    @SerializedName("movieId") val id: Long,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String? = null
)

data class MovieInfo(
    @SerializedName("movieId") val id: Long,
    @SerializedName("title") val title: String,
    @SerializedName("posterUrl") val posterUrl: String?,
    @SerializedName("ageRating") val ageRating: String?
)

data class ShowtimeInfo(
    @SerializedName("showtimeId") val id: Long,
    @SerializedName("startTime") val startTime: String,
    @SerializedName("endTime") val endTime: String,
    @SerializedName("format") val format: String,
    @SerializedName("basePrice") val basePrice: Double,
    @SerializedName("status") val status: String,
    @SerializedName("roomName") val roomName: String
)
