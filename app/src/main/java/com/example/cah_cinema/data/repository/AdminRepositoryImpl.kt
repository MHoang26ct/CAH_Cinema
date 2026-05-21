package com.example.cah_cinema.data.repository

import com.example.cah_cinema.data.model.*
import com.example.cah_cinema.data.remote.RetrofitClient
import com.example.cah_cinema.domain.repository.AdminRepository

class AdminRepositoryImpl : AdminRepository {
    override suspend fun getBusinessOverview(from: String, to: String): BaseResponse<BusinessOverviewResponse>? {
        return RetrofitClient.apiService.getBusinessOverview(from, to).body()
    }

    override suspend fun getDailyRevenue(from: String, to: String): BaseResponse<List<DailyRevenueResponse>>? {
        return RetrofitClient.apiService.getDailyRevenue(from, to).body()
    }

    override suspend fun getMovieRevenue(from: String, to: String): BaseResponse<List<MovieRevenueResponse>>? {
        return RetrofitClient.apiService.getMovieRevenue(from, to).body()
    }

    override suspend fun getCinemaRevenue(from: String, to: String): BaseResponse<List<CinemaRevenueResponse>>? {
        return RetrofitClient.apiService.getCinemaRevenue(from, to).body()
    }

    override suspend fun createMovie(request: UpdateOrCreateMovieRequest): BaseResponse<MovieDetail>? {
        return RetrofitClient.apiService.createMovie(request).body()
    }

    override suspend fun updateMovie(id: Long, request: UpdateOrCreateMovieRequest): BaseResponse<MovieDetail>? {
        return RetrofitClient.apiService.updateMovie(id, request).body()
    }

    override suspend fun deleteMovie(id: Long): BaseResponse<Unit>? {
        return RetrofitClient.apiService.deleteMovie(id).body()
    }

    override suspend fun getMovies(): BaseResponse<MoviePageData>? {
        return RetrofitClient.apiService.getMovies().body()
    }

    override suspend fun getCinemas(): BaseResponse<List<CinemaItem>>? {
        return RetrofitClient.apiService.getCinemas().body()
    }

    override suspend fun getCinemaDetail(cinemaId: Long): BaseResponse<CinemaItem>? {
        return RetrofitClient.apiService.getCinemaDetail(cinemaId).body()
    }

    override suspend fun createCinema(request: CreateCinemaRequest): BaseResponse<CinemaItem>? {
        return RetrofitClient.apiService.createCinema(request).body()
    }

    override suspend fun updateCinema(cinemaId: Long, request: CreateCinemaRequest): BaseResponse<CinemaItem>? {
        return RetrofitClient.apiService.updateCinema(cinemaId, request).body()
    }

    override suspend fun deleteCinema(id: Long): BaseResponse<Unit>? {
        return RetrofitClient.apiService.deleteCinema(id).body()
    }

    override suspend fun getRoomsByCinema(cinemaId: Long): BaseResponse<List<RoomItem>>? {
        return RetrofitClient.apiService.getRoomsByCinema(cinemaId).body()
    }

    override suspend fun createRoom(cinemaId: Long, request: CreateRoomRequest): BaseResponse<RoomItem>? {
        return RetrofitClient.apiService.createRoom(cinemaId, request).body()
    }

    override suspend fun updateRoom(roomId: Long, request: CreateRoomRequest): BaseResponse<RoomItem>? {
        return RetrofitClient.apiService.updateRoom(roomId, request).body()
    }

    override suspend fun deleteRoom(roomId: Long): BaseResponse<Unit>? {
        return RetrofitClient.apiService.deleteRoom(roomId).body()
    }

    override suspend fun getShowtimesByCinema(cinemaId: Long, date: String): BaseResponse<List<CinemaShowtimeItem>>? {
        return RetrofitClient.apiService.getShowtimesByCinema(cinemaId, date).body()
    }

    override suspend fun createShowtime(request: CreateShowtimeRequest): BaseResponse<ShowtimeInfo>? {
        return RetrofitClient.apiService.createShowtime(request).body()
    }

    override suspend fun updateShowtime(request: UpdateShowtimeRequest): BaseResponse<ShowtimeInfo>? {
        return RetrofitClient.apiService.updateShowtime(request).body()
    }

    override suspend fun deleteShowtime(id: Long): BaseResponse<Unit>? {
        return RetrofitClient.apiService.deleteShowtime(id).body()
    }

    override suspend fun getAllVouchers(page: Int): BaseResponse<List<VoucherItem>>? {
        return RetrofitClient.apiService.getAllVouchers(page).body()
    }

    override suspend fun getVoucherDetail(voucherId: Long): BaseResponse<VoucherItem>? {
        return RetrofitClient.apiService.getVoucherDetail(voucherId).body()
    }

    override suspend fun createVoucher(request: CreateVoucherRequest): BaseResponse<VoucherItem>? {
        return RetrofitClient.apiService.createVoucher(request).body()
    }

    override suspend fun updateVoucher(request: UpdateVoucherRequest): BaseResponse<VoucherItem>? {
        return RetrofitClient.apiService.updateVoucher(request).body()
    }

    override suspend fun deleteVoucher(voucherId: Long): BaseResponse<Unit>? {
        return RetrofitClient.apiService.deleteVoucher(voucherId).body()
    }

    override suspend fun getAllPriceConfigs(): BaseResponse<List<PriceConfig>>? {
        return RetrofitClient.apiService.getAllPriceConfigs().body()
    }

    override suspend fun updatePriceConfig(config: PriceConfig): BaseResponse<PriceConfig>? {
        return RetrofitClient.apiService.updatePriceConfig(config).body()
    }

    override suspend fun getAllHolidays(): BaseResponse<List<Holiday>>? {
        return RetrofitClient.apiService.getAllHolidays().body()
    }

    override suspend fun createHoliday(holiday: Holiday): BaseResponse<Holiday>? {
        return RetrofitClient.apiService.createHoliday(holiday).body()
    }

    override suspend fun updateHoliday(holiday: Holiday): BaseResponse<Holiday>? {
        return RetrofitClient.apiService.updateHoliday(holiday).body()
    }

    override suspend fun deleteHoliday(holidayId: Long): BaseResponse<Unit>? {
        return RetrofitClient.apiService.deleteHoliday(DeleteHolidayRequest(holidayId)).body()
    }

    override suspend fun getAllFood(): BaseResponse<List<FoodItem>>? {
        return RetrofitClient.apiService.getAdminFoods().body()
    }

    override suspend fun createFood(request: FoodItem): BaseResponse<FoodItem>? {
        return RetrofitClient.apiService.createFood(request).body()
    }

    override suspend fun updateFood(id: Long, request: FoodItem): BaseResponse<FoodItem>? {
        return RetrofitClient.apiService.updateFood(id, request).body()
    }

    override suspend fun deleteFood(id: Long): BaseResponse<Unit>? {
        return RetrofitClient.apiService.deleteFood(id).body()
    }

    override suspend fun createSeats(request: List<CreateSeatRequest>): BaseResponse<Unit>? {
        return RetrofitClient.apiService.createSeats(request).body()
    }

    override suspend fun deleteSeatsByRoom(roomId: Long): BaseResponse<Unit>? {
        return RetrofitClient.apiService.deleteSeatsByRoom(roomId).body()
    }
}
