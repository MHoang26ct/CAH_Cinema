package com.example.cah_cinema.presentation.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    object Home : Screen("home")
    object Cinema : Screen("cinema")
    object CinemaDetail : Screen("cinema_detail/{cinemaId}") {
        fun createRoute(cinemaId: String) = "cinema_detail/$cinemaId"
    }
    object MovieDetail : Screen("detail/{movieId}") {
        fun createRoute(movieId: String) = "detail/$movieId"
    }
    object TicketSelection : Screen("booking/{movieId}/{showtimeId}/{date}/{time}") {
        fun createRoute(movieId: String, showtimeId: String, date: String, time: String) = "booking/$movieId/$showtimeId/$date/$time"
    }
    object SeatSelection : Screen("seat_selection/{movieId}/{showtimeId}/{date}/{time}/{regularCount}/{coupleCount}/{basePrice}") {
        fun createRoute(movieId: String, showtimeId: String, date: String, time: String, regularCount: Int, coupleCount: Int, basePrice: Long) = 
            "seat_selection/$movieId/$showtimeId/$date/$time/$regularCount/$coupleCount/$basePrice"
    }
    object Concession : Screen("concession/{seats}/{totalAmount}/{date}/{time}") {
        fun createRoute(seats: String, totalAmount: Float, date: String, time: String) = "concession/$seats/$totalAmount/$date/$time"
    }
    object Payment : Screen("payment/{seats}/{totalAmount}/{date}/{time}") {
        fun createRoute(seats: String, totalAmount: Float, date: String, time: String) = "payment/$seats/$totalAmount/$date/$time"
    }
    object Notification : Screen("notification")
    object PromotionDetail : Screen("promotion_detail/{promotionId}") {
        fun createRoute(promotionId: String) = "promotion_detail/$promotionId"
    }
    object Profile : Screen("profile")
    object ChangePassword : Screen("change_password")
    object EditProfile : Screen("edit_profile")
    object TicketDetail : Screen("ticket_detail")
    object PaymentLoading : Screen("payment_loading")
    object UpcomingMovies : Screen("upcoming_movies")
}
