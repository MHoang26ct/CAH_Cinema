package com.example.cah_cinema.presentation.user.detail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cah_cinema.domain.model.Cinema
import com.example.cah_cinema.domain.model.Movie
import com.example.cah_cinema.domain.model.MovieDate
import com.example.cah_cinema.domain.model.Showtime
import com.example.cah_cinema.data.model.*
import com.example.cah_cinema.data.remote.RetrofitClient
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class MovieDetailState(
    val movie: Movie? = null,
    val availableDates: List<MovieDate> = emptyList(),
    val cinemas: List<Cinema> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    
    // Comments
    val comments: List<CommentItem> = emptyList(),
    val isCommentsLoading: Boolean = false,
    val commentText: String = "",
    val isSubmittingComment: Boolean = false,
    val canLoadMoreComments: Boolean = false,
    val currentCommentPage: Int = 0
)

class MovieDetailViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow(MovieDetailState())
    val state: StateFlow<MovieDetailState> = _state.asStateFlow()

    private val movieId: String? = savedStateHandle["movieId"]

    init {
        setupDates()
        loadMovieDetailAndShowtimes()
        loadComments()
    }

    private fun setupDates() {
        val calendar = Calendar.getInstance()
        val dayFormatter = SimpleDateFormat("EE", Locale("vi", "VN"))
        val dateFormatter = SimpleDateFormat("dd/MM", Locale.getDefault())

        val dates = (0..6).map { i ->
            val date = calendar.time
            val isToday = i == 0
            val movieDate = MovieDate(
                dayOfWeek = if (isToday) "Hôm nay" else dayFormatter.format(date),
                date = dateFormatter.format(date),
                isSelected = isToday
            )
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            movieDate
        }
        _state.update { it.copy(availableDates = dates) }
    }

    private fun loadMovieDetailAndShowtimes() {
        val id = movieId?.toLongOrNull() ?: return

        _state.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                // Gọi song song: chi tiết phim + showtimes ngày hôm nay
                val movieDeferred = async { RetrofitClient.apiService.getMovieDetail(id) }
                val showtimesDeferred = async {
                    val todayApiDate = getTodayApiDate()
                    RetrofitClient.apiService.getShowtimesByMovie(id, todayApiDate)
                }

                val movieResponse = movieDeferred.await()
                val showtimesResponse = showtimesDeferred.await()

                val movieDetail = if (movieResponse.isSuccessful) movieResponse.body()?.data else null
                val showtimesData = if (showtimesResponse.isSuccessful) showtimesResponse.body()?.data else null

                _state.update {
                    it.copy(
                        movie = movieDetail?.toDomainMovie(),
                        cinemas = showtimesData?.cinemas?.map { item -> item.toDomainCinema() } ?: emptyList(),
                        isLoading = false,
                        errorMessage = if (movieDetail == null) "Không thể tải chi tiết phim" else null
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun loadComments(page: Int = 0) {
        val id = movieId?.toLongOrNull() ?: return
        if (page == 0) _state.update { it.copy(isCommentsLoading = true) }

        viewModelScope.launch {
            try {
                Log.d("MovieDetail", "Loading comments for movie $id, page $page")
                val response = RetrofitClient.apiService.getMovieComments(id, page = page, size = 5)
                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d("MovieDetail", "Comments Response Body: $body")
                    val slice = body?.data
                    val newComments = slice?.content ?: emptyList()
                    Log.d("MovieDetail", "Loaded ${newComments.size} comments")
                    _state.update { currentState ->
                        currentState.copy(
                            comments = if (page == 0) newComments else currentState.comments + newComments,
                            canLoadMoreComments = slice?.last == false,
                            currentCommentPage = page,
                            isCommentsLoading = false
                        )
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("MovieDetail", "Load comments failed: ${response.code()} - $errorBody")
                    _state.update { it.copy(isCommentsLoading = false) }
                }
            } catch (e: Exception) {
                Log.e("MovieDetail", "Exception loading comments", e)
                _state.update { it.copy(isCommentsLoading = false) }
            }
        }
    }

    fun loadMoreComments() {
        if (_state.value.canLoadMoreComments && !_state.value.isCommentsLoading) {
            loadComments(_state.value.currentCommentPage + 1)
        }
    }

    fun onCommentTextChange(newText: String) {
        _state.update { it.copy(commentText = newText) }
    }

    fun submitComment() {
        val id = movieId?.toLongOrNull() ?: return
        val text = _state.value.commentText.trim()
        if (text.isEmpty()) return

        if (RetrofitClient.getToken() == null) {
            _state.update { it.copy(errorMessage = "Bạn cần đăng nhập để bình luận") }
            return
        }

        _state.update { it.copy(isSubmittingComment = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                Log.d("MovieDetail", "Submitting comment for movie $id: $text")
                val response = RetrofitClient.apiService.createMovieComment(id, CreateCommentRequest(text))
                if (response.isSuccessful) {
                    Log.d("MovieDetail", "Comment submitted successfully")
                    // Refresh comments from page 0
                    _state.update { it.copy(commentText = "", isSubmittingComment = false) }
                    loadComments(0)
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("MovieDetail", "Submit comment failed: ${response.code()} - $errorBody")
                    val msg = if (response.code() == 401) "Vui lòng đăng nhập lại" else "Lỗi: ${response.message()}"
                    _state.update { it.copy(isSubmittingComment = false, errorMessage = msg) }
                }
            } catch (e: Exception) {
                Log.e("MovieDetail", "Exception submitting comment", e)
                _state.update { it.copy(isSubmittingComment = false, errorMessage = e.message) }
            }
        }
    }

    fun deleteComment(commentId: Long) {
        viewModelScope.launch {
            try {
                Log.d("MovieDetail", "Deleting comment $commentId")
                val response = RetrofitClient.apiService.deleteMovieComment(commentId)
                if (response.isSuccessful) {
                    Log.d("MovieDetail", "Comment deleted")
                    loadComments(0)
                } else {
                    Log.e("MovieDetail", "Delete comment failed: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("MovieDetail", "Exception deleting comment", e)
            }
        }
    }
    
    fun clearErrorMessage() {
        _state.update { it.copy(errorMessage = null) }
    }

    private fun loadShowtimesForDate(date: String) {
        val id = movieId?.toLongOrNull() ?: return

        viewModelScope.launch {
            try {
                val currentYear = Calendar.getInstance().get(Calendar.YEAR)
                // date có thể là "dd/MM" hoặc "dd-MM" (do navigation encode "/" → "-")
                val normalizedDate = date.replace("-", "/")
                val parts = normalizedDate.split("/")
                val apiDate = if (parts.size == 2) "$currentYear-${parts[1]}-${parts[0]}" else return@launch

                val response = RetrofitClient.apiService.getShowtimesByMovie(id, apiDate)
                if (response.isSuccessful) {
                    val cinemas = response.body()?.data?.cinemas
                        ?.map { it.toDomainCinema() } ?: emptyList()
                    _state.update { it.copy(cinemas = cinemas) }
                } else {
                    _state.update { it.copy(cinemas = emptyList()) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(cinemas = emptyList()) }
            }
        }
    }

    private fun MovieDetail.toDomainMovie(): Movie {
        return Movie(
            id = this.id.toString(),
            title = this.title,
            genre = this.genres.joinToString(", ") { it.name },
            posterUrl = this.posterUrl,
            bannerUrl = this.posterUrl, // Tạm dùng poster làm banner
            duration = "${this.duration} phút",
            age = this.ageRating,
            director = this.directorName ?: "",
            cast = this.actorList ?: "",
            description = this.description,
            format = "2D",
            trailerUrl = this.trailerUrl
        )
    }

    private fun MovieShowtimeItem.toDomainCinema(): Cinema {
        return Cinema(
            id = this.cinemaId.toString(),
            name = this.cinemaName,
            address = this.address,
            showtimes = this.showtimes
                .filter { it.status == "AVAILABLE" }
                .map { it.toDomainShowtime() }
        )
    }

    private fun ShowtimeInfo.toDomainShowtime(): Showtime {
        // startTime có dạng "2025-05-17T18:20:00" hoặc "2025-05-17T18:20:00.000"
        val timeStr = try {
            val parts = this.startTime.split("T")
            if (parts.size == 2) parts[1].substring(0, 5) else this.startTime
        } catch (_: Exception) {
            this.startTime
        }
        return Showtime(
            id = this.id.toString(),
            time = timeStr,
            format = this.format
        )
    }

    private fun getTodayApiDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    fun onDateSelected(selectedDate: MovieDate) {
        _state.update { currentState ->
            currentState.copy(
                availableDates = currentState.availableDates.map {
                    it.copy(isSelected = it.date == selectedDate.date)
                }
            )
        }
        loadShowtimesForDate(selectedDate.date)
    }
}
