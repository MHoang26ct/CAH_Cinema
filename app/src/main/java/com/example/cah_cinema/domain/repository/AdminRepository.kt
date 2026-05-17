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
    suspend fun deleteMovie(id: Long): BaseResponse<Unit>?
    suspend fun getMovies(): BaseResponse<MoviePageData>?

    // Cinemas
    suspend fun getCinemas(): BaseResponse<List<CinemaItem>>?
    suspend fun getCinemaDetail(cinemaId: Long): BaseResponse<CinemaItem>?
    suspend fun createCinema(request: CreateCinemaRequest): BaseResponse<CinemaItem>?
    suspend fun updateCinema(cinemaId: Long, request: CreateCinemaRequest): BaseResponse<CinemaItem>?
    suspend fun deleteCinema(id: Long): BaseResponse<Unit>?
    suspend fun getRoomsByCinema(cinemaId: Long): BaseResponse<List<RoomItem>>?
    suspend fun createRoom(cinemaId: Long, request: CreateRoomRequest): BaseResponse<RoomItem>?
    suspend fun updateRoom(roomId: Long, request: CreateRoomRequest): BaseResponse<RoomItem>?
    suspend fun deleteRoom(roomId: Long): BaseResponse<Unit>?

    // Showtimes
    suspend fun getShowtimesByCinema(cinemaId: Long, date: String): BaseResponse<List<CinemaShowtimeItem>>?
    suspend fun createShowtime(request: CreateShowtimeRequest): BaseResponse<ShowtimeInfo>?
    suspend fun updateShowtime(request: UpdateShowtimeRequest): BaseResponse<ShowtimeInfo>?
    suspend fun deleteShowtime(id: Long): BaseResponse<Unit>?

    // Vouchers
    suspend fun getAllVouchers(page: Int): BaseResponse<List<VoucherItem>>?
    suspend fun getVoucherDetail(voucherId: Long): BaseResponse<VoucherItem>?
    suspend fun createVoucher(request: CreateVoucherRequest): BaseResponse<VoucherItem>?
    suspend fun updateVoucher(request: UpdateVoucherRequest): BaseResponse<VoucherItem>?
    suspend fun deleteVoucher(voucherId: Long): BaseResponse<Unit>?

    // Price Config & Holiday
    suspend fun getAllPriceConfigs(): BaseResponse<List<PriceConfig>>?
    suspend fun updatePriceConfig(config: PriceConfig): BaseResponse<PriceConfig>?
    suspend fun getAllHolidays(): BaseResponse<List<Holiday>>?
    suspend fun createHoliday(holiday: Holiday): BaseResponse<Holiday>?
    suspend fun updateHoliday(holiday: Holiday): BaseResponse<Holiday>?
    suspend fun deleteHoliday(holidayId: Long): BaseResponse<Unit>?

    // Seats
    suspend fun createSeats(request: List<CreateSeatRequest>): BaseResponse<Unit>?
    suspend fun deleteSeatsByRoom(roomId: Long): BaseResponse<Unit>?
}
