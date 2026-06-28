package com.example.cah_cinema.presentation.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    object OtpVerification : Screen("otp_verification/{email}") {
        fun createRoute(email: String) = "otp_verification/$email"
    }
    object ResetPassword : Screen("reset_password/{email}/{resetToken}") {
        fun createRoute(email: String, resetToken: String) = "reset_password/$email/$resetToken"
    }
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
    object Concession : Screen("concession/{movieId}/{showtimeId}/{seatIds}/{seatsDisplay}/{totalAmount}/{date}/{time}") {
        fun createRoute(movieId: String, showtimeId: String, seatIds: String, seatsDisplay: String, totalAmount: Float, date: String, time: String) = 
            "concession/$movieId/$showtimeId/$seatIds/$seatsDisplay/$totalAmount/$date/$time"
    }
    object Payment : Screen("payment/{movieId}/{showtimeId}/{seatIds}/{seatsDisplay}/{totalAmount}/{date}/{time}") {
        fun createRoute(movieId: String, showtimeId: String, seatIds: String, seatsDisplay: String, totalAmount: Float, date: String, time: String) = 
            "payment/$movieId/$showtimeId/$seatIds/$seatsDisplay/$totalAmount/$date/$time"
    }
    object Notification : Screen("notification")
    object PromotionDetail : Screen("promotion_detail/{promotionId}") {
        fun createRoute(promotionId: String) = "promotion_detail/$promotionId"
    }
    object Profile : Screen("profile")
    object ChangePassword : Screen("change_password")
    object EditProfile : Screen("edit_profile")
    object TicketDetail : Screen("ticket_detail")
    object BookingHistory : Screen("booking_history")
    object PaymentLoading : Screen("payment_loading")
    object UpcomingMovies : Screen("upcoming_movies")
    object Voucher : Screen("voucher/{totalAmount}") {
        fun createRoute(totalAmount: Float) = "voucher/$totalAmount"
    }
    // Admin Screens
    object AdminDashboard : Screen("admin_dashboard")
    object AdminMovieManagement : Screen("admin_movies")
    object AdminCinemaManagement : Screen("admin_cinemas")
    object AdminPromotionManagement : Screen("admin_promotions")
    object AdminVoucherManagement : Screen("admin_vouchers")
    object AdminShowtimeManagement : Screen("admin_showtimes")
    object AdminFoodManagement : Screen("admin_food")
    object AdminReport : Screen("admin_reports")
    object AdminSeatManagement : Screen("admin_seats/{roomId}/{cinemaId}") {
        fun createRoute(roomId: Long, cinemaId: Long) = "admin_seats/$roomId/$cinemaId"
    }
    object AdminSettings : Screen("admin_settings")

    // Staff Screens
    object StaffDashboard : Screen("staff_dashboard")
    object StaffCheckIn : Screen("staff_checkin")
    object StaffSellTicket : Screen("staff_sell_ticket")
    object StaffSellSeatSelection : Screen("staff_sell_seats/{showtimeId}/{movieTitle}/{moviePosterUrl}/{cinemaName}/{roomName}/{startTime}/{basePrice}") {
        fun createRoute(
            showtimeId: Long,
            movieTitle: String,
            moviePosterUrl: String,
            cinemaName: String,
            roomName: String,
            startTime: String,
            basePrice: Double
        ) = "staff_sell_seats/${showtimeId}/${android.net.Uri.encode(movieTitle)}/${android.net.Uri.encode(moviePosterUrl)}/${android.net.Uri.encode(cinemaName)}/${android.net.Uri.encode(roomName)}/${android.net.Uri.encode(startTime)}/$basePrice"
    }
    object StaffSellPayment : Screen("staff_sell_payment/{showtimeId}/{seatIds}/{seatsDisplay}/{totalAmount}/{movieTitle}/{cinemaName}/{startTime}") {
        fun createRoute(
            showtimeId: Long,
            seatIds: String,
            seatsDisplay: String,
            totalAmount: Double,
            movieTitle: String,
            cinemaName: String,
            startTime: String
        ) = "staff_sell_payment/$showtimeId/${android.net.Uri.encode(seatIds)}/${android.net.Uri.encode(seatsDisplay)}/$totalAmount/${android.net.Uri.encode(movieTitle)}/${android.net.Uri.encode(cinemaName)}/${android.net.Uri.encode(startTime)}"
    }
}
