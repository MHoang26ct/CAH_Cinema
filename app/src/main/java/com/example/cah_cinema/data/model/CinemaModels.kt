package com.example.cah_cinema.data.model

import com.google.gson.annotations.SerializedName

data class CinemaItem(
    @SerializedName("cinemaId") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("address") val address: String,
    @SerializedName("hotline") val hotline: String,
    @SerializedName("imageUrl") val imageUrl: String? = null
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

data class MovieInfo(
    @SerializedName("movieId") val id: Long,
    @SerializedName("title") val title: String,
    @SerializedName("posterUrl") val posterUrl: String?,
    @SerializedName("ageRating") val ageRating: String?
)
