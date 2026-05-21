package com.example.cah_cinema.data.repository

import com.example.cah_cinema.data.model.*
import com.example.cah_cinema.data.remote.RetrofitClient
import com.example.cah_cinema.domain.repository.AdminRepository
import retrofit2.Response

class AdminRepositoryImpl : AdminRepository {

    private fun <T> handle(r: Response<BaseResponse<T>>): BaseResponse<T>? {
        return if (r.isSuccessful) {
            val body = r.body()
            if (body != null && body.code == 0) body.copy(code = r.code()) else body
        } else {
            BaseResponse(r.code(), r.message(), null)
        }
    }

    // ── Reports ──────────────────────────────────────────────────────────────

    override suspend fun getBusinessOverview(from: String, to: String): BaseResponse<BusinessOverviewResponse>? =
        handle(RetrofitClient.apiService.getBusinessOverview(from, to))

    override suspend fun getDailyRevenue(from: String, to: String): BaseResponse<List<DailyRevenueResponse>>? =
        handle(RetrofitClient.apiService.getDailyRevenue(from, to))

    override suspend fun getMovieRevenue(from: String, to: String): BaseResponse<List<MovieRevenueResponse>>? =
        handle(RetrofitClient.apiService.getMovieRevenue(from, to))

    override suspend fun getCinemaRevenue(from: String, to: String): BaseResponse<List<CinemaRevenueResponse>>? =
        handle(RetrofitClient.apiService.getCinemaRevenue(from, to))

    // ── Movies ───────────────────────────────────────────────────────────────

    override suspend fun getMovies(page: Int, size: Int, sort: String?): BaseResponse<MoviePageData>? =
        handle(RetrofitClient.apiService.getMovies(page = page, size = size, sort = sort))

    override suspend fun createMovie(request: UpdateOrCreateMovieRequest): BaseResponse<MovieDetail>? =
        handle(RetrofitClient.apiService.createMovie(request))

    override suspend fun updateMovie(id: Long, request: UpdateOrCreateMovieRequest): BaseResponse<MovieDetail>? =
        handle(RetrofitClient.apiService.updateMovie(id, request))

    override suspend fun deleteMovie(id: Long): BaseResponse<Unit>? =
        handle(RetrofitClient.apiService.deleteMovie(id))

    // ── Cinemas ──────────────────────────────────────────────────────────────

    override suspend fun getCinemas(): BaseResponse<List<CinemaItem>>? =
        handle(RetrofitClient.apiService.getCinemas())

    override suspend fun getCinemaDetail(cinemaId: Long): BaseResponse<CinemaItem>? =
        handle(RetrofitClient.apiService.getCinemaDetail(cinemaId))

    override suspend fun createCinema(request: CreateCinemaRequest): BaseResponse<CinemaItem>? =
        handle(RetrofitClient.apiService.createCinema(request))

    override suspend fun updateCinema(cinemaId: Long, request: CreateCinemaRequest): BaseResponse<CinemaItem>? =
        handle(RetrofitClient.apiService.updateCinema(cinemaId, request))

    override suspend fun deleteCinema(id: Long): BaseResponse<Unit>? =
        handle(RetrofitClient.apiService.deleteCinema(id))

    override suspend fun getRoomsByCinema(cinemaId: Long): BaseResponse<List<RoomItem>>? =
        handle(RetrofitClient.apiService.getRoomsByCinema(cinemaId))

    override suspend fun createRoom(cinemaId: Long, request: CreateRoomRequest): BaseResponse<RoomItem>? =
        handle(RetrofitClient.apiService.createRoom(cinemaId, request))

    override suspend fun updateRoom(roomId: Long, request: CreateRoomRequest): BaseResponse<RoomItem>? =
        handle(RetrofitClient.apiService.updateRoom(roomId, request))

    override suspend fun deleteRoom(roomId: Long): BaseResponse<Unit>? =
        handle(RetrofitClient.apiService.deleteRoom(roomId))

    // ── Showtimes ────────────────────────────────────────────────────────────

    override suspend fun getShowtimesByCinema(cinemaId: Long, date: String): BaseResponse<List<CinemaShowtimeItem>>? =
        handle(RetrofitClient.apiService.getShowtimesByCinema(cinemaId, date))

    override suspend fun createShowtime(request: CreateShowtimeRequest): BaseResponse<Unit>? =
        handle(RetrofitClient.apiService.createShowtime(request))

    override suspend fun updateShowtime(request: UpdateShowtimeRequest): BaseResponse<Unit>? =
        handle(RetrofitClient.apiService.updateShowtime(request))

    override suspend fun deleteShowtime(id: Long): BaseResponse<Unit>? =
        handle(RetrofitClient.apiService.deleteShowtime(id))

    // ── Vouchers ─────────────────────────────────────────────────────────────

    override suspend fun getAllVouchers(page: Int): BaseResponse<SliceResponse<VoucherItem>>? =
        handle(RetrofitClient.apiService.getAllVouchers(page))

    override suspend fun getVoucherDetail(voucherId: Long): BaseResponse<VoucherItem>? =
        handle(RetrofitClient.apiService.getVoucherDetail(voucherId))

    override suspend fun createVoucher(request: CreateVoucherRequest): BaseResponse<VoucherItem>? =
        handle(RetrofitClient.apiService.createVoucher(request))

    override suspend fun updateVoucher(request: UpdateVoucherRequest): BaseResponse<VoucherItem>? =
        handle(RetrofitClient.apiService.updateVoucher(request))

    override suspend fun deleteVoucher(voucherId: Long): BaseResponse<Unit>? =
        handle(RetrofitClient.apiService.deleteVoucher(voucherId))

    // ── Price Config & Holiday ────────────────────────────────────────────────

    override suspend fun getAllPriceConfigs(): BaseResponse<List<PriceConfig>>? =
        handle(RetrofitClient.apiService.getAllPriceConfigs())

    override suspend fun updatePriceConfig(config: PriceConfig): BaseResponse<PriceConfig>? =
        handle(RetrofitClient.apiService.updatePriceConfig(config))

    override suspend fun getAllHolidays(): BaseResponse<List<Holiday>>? =
        handle(RetrofitClient.apiService.getAllHolidays())

    override suspend fun createHoliday(holiday: Holiday): BaseResponse<Holiday>? =
        handle(RetrofitClient.apiService.createHoliday(holiday))

    override suspend fun updateHoliday(holiday: Holiday): BaseResponse<Holiday>? =
        handle(RetrofitClient.apiService.updateHoliday(holiday))

    override suspend fun deleteHoliday(holidayId: Long): BaseResponse<Unit>? =
        handle(RetrofitClient.apiService.deleteHoliday(DeleteHolidayRequest(holidayId)))

    // ── Seats ─────────────────────────────────────────────────────────────────

    override suspend fun createSeats(request: List<CreateSeatRequest>): BaseResponse<Unit>? =
        handle(RetrofitClient.apiService.createSeats(request))

    override suspend fun deleteSeatsByRoom(roomId: Long): BaseResponse<Unit>? =
        handle(RetrofitClient.apiService.deleteSeatsByRoom(roomId))

    // ── Food ──────────────────────────────────────────────────────────────────

    override suspend fun getFoods(): BaseResponse<List<FoodItem>>? =
        handle(RetrofitClient.apiService.getAdminFoods())

    override suspend fun createFood(request: FoodItem): BaseResponse<FoodItem>? =
        handle(RetrofitClient.apiService.createFood(request))

    override suspend fun updateFood(id: Long, request: FoodItem): BaseResponse<FoodItem>? =
        handle(RetrofitClient.apiService.updateFood(id, request))

    override suspend fun deleteFood(id: Long): BaseResponse<Unit>? =
        handle(RetrofitClient.apiService.deleteFood(id))

    // ── Promotions ────────────────────────────────────────────────────────────

    override suspend fun getAdminPromotions(page: Int): BaseResponse<SliceResponse<AdminPromotionItem>>? =
        handle(RetrofitClient.apiService.getAdminPromotions(page))

    override suspend fun getAdminPromotionDetail(id: Long): BaseResponse<AdminPromotionDetail>? =
        handle(RetrofitClient.apiService.getAdminPromotionDetail(id))

    override suspend fun createPromotion(request: CreateOrUpdatePromotionRequest): BaseResponse<AdminPromotionDetail>? =
        handle(RetrofitClient.apiService.createPromotion(request))

    override suspend fun updatePromotion(id: Long, request: CreateOrUpdatePromotionRequest): BaseResponse<AdminPromotionDetail>? =
        handle(RetrofitClient.apiService.updatePromotion(id, request))

    override suspend fun deletePromotion(id: Long): BaseResponse<Unit>? =
        handle(RetrofitClient.apiService.deletePromotion(id))
}
