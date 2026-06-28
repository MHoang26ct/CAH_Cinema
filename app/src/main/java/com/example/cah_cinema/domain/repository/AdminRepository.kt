package com.example.cah_cinema.domain.repository

import com.example.cah_cinema.data.model.*

interface AdminRepository {
    // Reports
    suspend fun getBusinessOverview(from: String, to: String): BaseResponse<BusinessOverviewResponse>?
    suspend fun getDailyRevenue(from: String, to: String): BaseResponse<List<DailyRevenueResponse>>?
    suspend fun getMovieRevenue(from: String, to: String): BaseResponse<List<MovieRevenueResponse>>?
    suspend fun getCinemaRevenue(from: String, to: String): BaseResponse<List<CinemaRevenueResponse>>?

    // Movies
    suspend fun createMovie(request: UpdateOrCreateMovieRequest): BaseResponse<MovieDetail>?
    suspend fun updateMovie(id: Long, request: UpdateOrCreateMovieRequest): BaseResponse<MovieDetail>?
    suspend fun deleteMovie(id: Long): BaseResponse<String>?
    suspend fun getMovies(page: Int = 0, size: Int = 20, sort: String? = null): BaseResponse<MoviePageData>?

    // Cinemas
    suspend fun getCinemas(): BaseResponse<List<CinemaItem>>?
    suspend fun getCinemaDetail(cinemaId: Long): BaseResponse<CinemaItem>?
    suspend fun createCinema(request: CreateCinemaRequest): BaseResponse<CinemaItem>?
    suspend fun updateCinema(cinemaId: Long, request: UpdateCinemaRequest): BaseResponse<CinemaItem>?
    suspend fun deleteCinema(id: Long): BaseResponse<String>?
    suspend fun getRoomsByCinema(cinemaId: Long): BaseResponse<List<RoomItem>>?
    suspend fun createRoom(cinemaId: Long, request: CreateRoomRequest): BaseResponse<RoomItem>?
    suspend fun updateRoom(roomId: Long, request: CreateRoomRequest): BaseResponse<RoomItem>?
    suspend fun deleteRoom(roomId: Long): BaseResponse<String>?

    // Showtimes
    suspend fun getShowtimesByCinema(cinemaId: Long, date: String): BaseResponse<List<CinemaShowtimeItem>>?
    suspend fun getShowtimesByRoom(roomId: Long, date: String): BaseResponse<List<ShowtimeInfo>>?
    suspend fun createShowtime(request: CreateShowtimeRequest): BaseResponse<String>?
    suspend fun updateShowtime(request: UpdateShowtimeRequest): BaseResponse<String>?
    suspend fun deleteShowtime(id: Long): BaseResponse<String>?
    suspend fun cancelShowtimesByRoom(request: CancelShowtimesByRoomRequest): BaseResponse<String>?
    suspend fun getShowtimeSeats(showtimeId: Long): BaseResponse<List<SeatItem>>?

    // Vouchers
    suspend fun getAllVouchers(page: Int): BaseResponse<SliceResponse<VoucherItem>>?
    suspend fun getVoucherDetail(voucherId: Long): BaseResponse<VoucherItem>?
    suspend fun createVoucher(request: CreateVoucherRequest): BaseResponse<VoucherItem>?
    suspend fun updateVoucher(request: UpdateVoucherRequest): BaseResponse<VoucherItem>?
    suspend fun deleteVoucher(voucherId: Long): BaseResponse<String>?

    // Price Config & Holiday
    suspend fun getAllPriceConfigs(): BaseResponse<List<PriceConfig>>?
    suspend fun updatePriceConfig(config: PriceConfig): BaseResponse<PriceConfig>?
    suspend fun getAllHolidays(): BaseResponse<List<Holiday>>?
    suspend fun createHoliday(holiday: Holiday): BaseResponse<Holiday>?
    suspend fun updateHoliday(holiday: Holiday): BaseResponse<Holiday>?
    suspend fun deleteHoliday(holidayId: Long): BaseResponse<String>?

    // Seats
    suspend fun getSeatsByRoom(roomId: Long): BaseResponse<List<SeatItem>>?
    suspend fun createSeats(request: List<CreateSeatRequest>): BaseResponse<String>?
    suspend fun replaceSeatMap(request: ReplaceSeatMapRequest): BaseResponse<String>?
    suspend fun deleteSeatsByRoom(roomId: Long): BaseResponse<String>?

    // Food
    suspend fun getFoods(): BaseResponse<List<FoodItem>>?
    suspend fun createFood(request: FoodItem): BaseResponse<FoodItem>?
    suspend fun updateFood(id: Long, request: FoodItem): BaseResponse<FoodItem>?
    suspend fun deleteFood(id: Long): BaseResponse<String>?

    // Promotions
    suspend fun getAdminPromotions(page: Int): BaseResponse<SliceResponse<AdminPromotionItem>>?
    suspend fun getAdminPromotionDetail(id: Long): BaseResponse<AdminPromotionDetail>?
    suspend fun createPromotion(request: CreateOrUpdatePromotionRequest): BaseResponse<AdminPromotionDetail>?
    suspend fun updatePromotion(id: Long, request: CreateOrUpdatePromotionRequest): BaseResponse<AdminPromotionDetail>?
    suspend fun deletePromotion(id: Long): BaseResponse<String>?
}
