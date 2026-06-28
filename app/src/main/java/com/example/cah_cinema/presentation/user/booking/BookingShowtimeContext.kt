package com.example.cah_cinema.presentation.user.booking

import com.example.cah_cinema.data.model.MovieDetail
import com.example.cah_cinema.data.model.ShowtimeInfo
import com.example.cah_cinema.data.remote.RetrofitClient
import com.example.cah_cinema.domain.model.Movie
import com.example.cah_cinema.util.DateTimeUtils

data class BookingShowtimeContext(
    val movie: Movie? = null,
    val cinemaName: String = "",
    val roomName: String = "",
    val basePrice: Double = 0.0,
    val showtimeFormat: String = "2D",
    val otherShowtimes: List<String> = emptyList()
)

suspend fun loadBookingShowtimeContext(
    movieId: Long,
    showtimeId: Long,
    navDate: String
): BookingShowtimeContext {
    var cinemaName = ""
    var roomName = ""
    var basePrice = 0.0
    var showtimeFormat = "2D"
    var otherShowtimes = emptyList<String>()
    var movie: Movie? = null

    val movieResponse = RetrofitClient.apiService.getMovieDetail(movieId)
    val movieDetail = if (movieResponse.isSuccessful) movieResponse.body()?.data else null
    movie = movieDetail?.toBookingMovie()

    val apiDate = DateTimeUtils.navDateToApiDate(navDate)
    if (!apiDate.isNullOrEmpty()) {
        try {
            val showtimesResponse = RetrofitClient.apiService.getShowtimesByMovie(movieId, apiDate)
            if (showtimesResponse.isSuccessful) {
                showtimesResponse.body()?.data?.cinemas?.forEach { cinema ->
                    val matched = cinema.showtimes.find { it.id == showtimeId }
                    if (matched != null) {
                        cinemaName = cinema.cinemaName
                        roomName = matched.roomName
                        basePrice = matched.basePrice
                        showtimeFormat = matched.format
                        otherShowtimes = cinema.showtimes.map { formatShowtimeTime(it) }
                    }
                }
            }
        } catch (_: Exception) { }
    }

    return BookingShowtimeContext(
        movie = movie,
        cinemaName = cinemaName,
        roomName = roomName,
        basePrice = basePrice,
        showtimeFormat = showtimeFormat,
        otherShowtimes = otherShowtimes
    )
}

private fun formatShowtimeTime(showtime: ShowtimeInfo): String {
    return showtime.startTime.split("T").getOrNull(1)?.substring(0, 5) ?: showtime.startTime
}

private fun MovieDetail.toBookingMovie(): Movie {
    return Movie(
        id = id.toString(),
        title = title,
        genre = genres.joinToString(", ") { it.name },
        posterUrl = posterUrl,
        age = ageRating,
        duration = "$duration phút",
        format = "2D"
    )
}
