package com.example.cah_cinema.data.remote

import com.example.cah_cinema.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    // 1. Authentication
    @POST("api/v1/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<BaseResponse<LoginData>>

    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<BaseResponse<LoginData>>

    @POST("api/v1/auth/google")
    suspend fun googleLogin(@Body request: GoogleLoginRequest): Response<BaseResponse<LoginData>>

    @POST("api/v1/auth/send-otp")
    suspend fun sendOtp(@Body request: OtpRequest): Response<BaseResponse<Unit>>

    @POST("api/v1/auth/verify-otp")
    suspend fun verifyOtp(@Body request: OtpVerifyRequest): Response<BaseResponse<Boolean>>

    @POST("api/v1/auth/fp-verify-otp")
    suspend fun verifyForgotPasswordOtp(@Body request: OtpVerifyRequest): Response<BaseResponse<OtpVerifyResponse>>

    @POST("api/v1/auth/fp-change-password")
    suspend fun changeForgotPassword(@Body request: ResetPasswordRequest): Response<BaseResponse<Unit>>

    @POST("api/v1/auth/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<BaseResponse<Unit>>

    // 2. Movies
    @GET("api/v1/public/movies/featured")
    suspend fun getFeaturedMovies(): Response<BaseResponse<FeaturedMoviesData>>

    @GET("api/v1/public/movies")
    suspend fun getMovies(
        @Query("title") title: String? = null,
        @Query("genreId") genreId: Long? = null,
        @Query("ageRating") ageRating: String? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("sort") sort: String? = null
    ): Response<BaseResponse<MoviePageData>>

    @GET("api/v1/public/movies/{id}")
    suspend fun getMovieDetail(@Path("id") id: Long): Response<BaseResponse<MovieDetail>>

    @GET("api/v1/public/genres/all")
    suspend fun getAllGenres(): Response<BaseResponse<List<Genre>>>

    // 3. Cinemas
    @GET("api/v1/public/cinemas")
    suspend fun getCinemas(): Response<BaseResponse<List<CinemaItem>>>

    // 4. Showtimes
    @GET("api/v1/public/showtimes/movies/{movieId}")
    suspend fun getShowtimesByMovie(
        @Path("movieId") movieId: Long,
        @Query("date") date: String // format: yyyy-MM-dd
    ): Response<BaseResponse<MovieShowtimesResponse>>

    @GET("api/v1/public/showtimes/cinemas/{cinemaId}")
    suspend fun getShowtimesByCinema(
        @Path("cinemaId") cinemaId: Long,
        @Query("date") date: String // format: yyyy-MM-dd
    ): Response<BaseResponse<List<CinemaShowtimeItem>>>

    // 5. Seats & Booking
    @GET("api/v1/public/seats")
    suspend fun getSeats(@Query("showtimeId") showtimeId: Long): Response<BaseResponse<List<SeatItem>>>

    @POST("api/v1/seats/{seatId}/lock")
    suspend fun lockSeat(
        @Path("seatId") seatId: Long,
        @Query("showtimeId") showtimeId: Long
    ): Response<BaseResponse<Unit>>

    @DELETE("api/v1/seats/{seatId}/lock")
    suspend fun unlockSeat(
        @Path("seatId") seatId: Long,
        @Query("showtimeId") showtimeId: Long
    ): Response<BaseResponse<Unit>>

    @POST("api/v1/seats/pre-lock")
    suspend fun preLockSeats(@Body request: PreLockRequest): Response<BaseResponse<Unit>>

    @POST("api/v1/bookings")
    suspend fun createBooking(@Body request: CreateBookingRequest): Response<BaseResponse<BookingData>>

    @POST("api/v1/bookings/{bookingId}/confirm-payment")
    suspend fun confirmPayment(
        @Path("bookingId") bookingId: Long,
        @Body request: ConfirmPaymentRequest
    ): Response<BaseResponse<Unit>>

    // 6. Vouchers & Food
    @GET("api/v1/user/vouchers")
    suspend fun getMyVouchers(): Response<BaseResponse<List<VoucherItem>>>

    @GET("api/v1/user/food")
    suspend fun getFoods(): Response<BaseResponse<List<FoodItem>>>

    // 9. Profile
    @GET("api/v1/users/me")
    suspend fun getMyProfile(): Response<BaseResponse<ProfileData>>

    @PATCH("api/v1/users/me")
    suspend fun updateMyProfile(@Body request: UpdateProfileRequest): Response<BaseResponse<UserInfo>>

    // --- ADMIN ENDPOINTS ---

    // Admin Reports
    @GET("api/v1/admin/reports/overview")
    suspend fun getBusinessOverview(
        @Query("from") from: String,
        @Query("to") to: String
    ): Response<BaseResponse<BusinessOverviewResponse>>

    @GET("api/v1/admin/reports/revenue/daily")
    suspend fun getDailyRevenue(
        @Query("from") from: String,
        @Query("to") to: String
    ): Response<BaseResponse<List<DailyRevenueResponse>>>

    @GET("api/v1/admin/reports/revenue/by-movie")
    suspend fun getMovieRevenue(
        @Query("from") from: String,
        @Query("to") to: String
    ): Response<BaseResponse<List<MovieRevenueResponse>>>

    @GET("api/v1/admin/reports/revenue/by-cinema")
    suspend fun getCinemaRevenue(
        @Query("from") from: String,
        @Query("to") to: String
    ): Response<BaseResponse<List<CinemaRevenueResponse>>>

    // Admin Movies
    @POST("api/v1/admin/movies/create")
    suspend fun createMovie(@Body request: UpdateOrCreateMovieRequest): Response<BaseResponse<MovieDetail>>

    @PUT("api/v1/admin/movies/update/{id}")
    suspend fun updateMovie(
        @Path("id") id: Long,
        @Body request: UpdateOrCreateMovieRequest
    ): Response<BaseResponse<MovieDetail>>

    @DELETE("api/v1/admin/movies/delete/{id}")
    suspend fun deleteMovie(@Path("id") id: Long): Response<BaseResponse<Unit>>

    // Admin Cinemas
    @GET("api/v1/admin/cinemas/{cinemaId}")
    suspend fun getCinemaDetail(@Path("cinemaId") cinemaId: Long): Response<BaseResponse<CinemaItem>>

    @POST("api/v1/admin/cinemas")
    suspend fun createCinema(@Body request: CreateCinemaRequest): Response<BaseResponse<CinemaItem>>

    @PUT("api/v1/admin/cinemas/{cinemaId}")
    suspend fun updateCinema(
        @Path("cinemaId") cinemaId: Long,
        @Body request: CreateCinemaRequest
    ): Response<BaseResponse<CinemaItem>>

    @DELETE("api/v1/admin/cinemas/{cinemaId}")
    suspend fun deleteCinema(@Path("cinemaId") cinemaId: Long): Response<BaseResponse<Unit>>

    @GET("api/v1/admin/cinemas/{cinemaId}/rooms")
    suspend fun getRoomsByCinema(@Path("cinemaId") cinemaId: Long): Response<BaseResponse<List<RoomItem>>>

    @POST("api/v1/admin/cinemas/{cinemaId}/rooms")
    suspend fun createRoom(
        @Path("cinemaId") cinemaId: Long,
        @Body request: CreateRoomRequest
    ): Response<BaseResponse<RoomItem>>

    @PUT("api/v1/admin/cinemas/rooms/{roomId}")
    suspend fun updateRoom(
        @Path("roomId") roomId: Long,
        @Body request: CreateRoomRequest
    ): Response<BaseResponse<RoomItem>>

    @DELETE("api/v1/admin/cinemas/rooms/{roomId}")
    suspend fun deleteRoom(@Path("roomId") roomId: Long): Response<BaseResponse<Unit>>

    // Admin Showtimes
    @POST("api/v1/admin/showtime")
    suspend fun createShowtime(@Body request: CreateShowtimeRequest): Response<BaseResponse<Unit>>

    @PUT("api/v1/admin/showtime")
    suspend fun updateShowtime(@Body request: UpdateShowtimeRequest): Response<BaseResponse<Unit>>

    @DELETE("api/v1/admin/showtime/{showtimeId}")
    suspend fun deleteShowtime(@Path("showtimeId") showtimeId: Long): Response<BaseResponse<Unit>>

    // Admin Vouchers
    @GET("api/v1/admin/vouchers")
    suspend fun getAllVouchers(@Query("page") page: Int = 0): Response<BaseResponse<SliceResponse<VoucherItem>>>

    @GET("api/v1/admin/vouchers/{voucherId}")
    suspend fun getVoucherDetail(@Path("voucherId") voucherId: Long): Response<BaseResponse<VoucherItem>>

    @POST("api/v1/admin/vouchers/create")
    suspend fun createVoucher(@Body request: CreateVoucherRequest): Response<BaseResponse<VoucherItem>>

    @POST("api/v1/admin/vouchers/update")
    suspend fun updateVoucher(@Body request: UpdateVoucherRequest): Response<BaseResponse<VoucherItem>>

    @DELETE("api/v1/admin/vouchers/{voucherId}")
    suspend fun deleteVoucher(@Path("voucherId") voucherId: Long): Response<BaseResponse<Unit>>

    // Admin Seats
    @POST("api/v1/admin/seats/create")
    suspend fun createSeats(@Body request: List<CreateSeatRequest>): Response<BaseResponse<Unit>>

    @DELETE("api/v1/admin/seats/delete/{roomId}")
    suspend fun deleteSeatsByRoom(@Path("roomId") roomId: Long): Response<BaseResponse<Unit>>

    // Admin Price Config
    @GET("api/v1/admin/price-config/all")
    suspend fun getAllPriceConfigs(): Response<BaseResponse<List<PriceConfig>>>

    @POST("api/v1/admin/price-config/update")
    suspend fun updatePriceConfig(@Body request: PriceConfig): Response<BaseResponse<PriceConfig>>

    // Admin Holiday
    @GET("api/v1/admin/holiday/all")
    suspend fun getAllHolidays(): Response<BaseResponse<List<Holiday>>>

    @POST("api/v1/admin/holiday/create")
    suspend fun createHoliday(@Body request: Holiday): Response<BaseResponse<Holiday>>

    @POST("api/v1/admin/holiday/update")
    suspend fun updateHoliday(@Body request: Holiday): Response<BaseResponse<Holiday>>

    @HTTP(method = "DELETE", path = "api/v1/admin/holiday/delete", hasBody = true)
    suspend fun deleteHoliday(@Body request: DeleteHolidayRequest): Response<BaseResponse<Unit>>

    // Admin Food
    @GET("api/v1/admin/food")
    suspend fun getAdminFoods(): Response<BaseResponse<List<FoodItem>>>

    @POST("api/v1/admin/food")
    suspend fun createFood(@Body request: FoodItem): Response<BaseResponse<FoodItem>>

    @PUT("api/v1/admin/food/{id}")
    suspend fun updateFood(@Path("id") id: Long, @Body request: FoodItem): Response<BaseResponse<FoodItem>>

    @DELETE("api/v1/admin/food/{id}")
    suspend fun deleteFood(@Path("id") id: Long): Response<BaseResponse<Unit>>
}
