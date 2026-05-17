package com.example.cah_cinema.data.repository

import com.example.cah_cinema.domain.model.Concession
import com.example.cah_cinema.domain.model.ConcessionType
import com.example.cah_cinema.domain.repository.ConcessionRepository
import com.example.cah_cinema.util.ImageUrls
import kotlinx.coroutines.delay

class ConcessionRepositoryImpl : ConcessionRepository {
    override suspend fun getConcessions(): Result<List<Concession>> {
        // Giả lập độ trễ mạng
        delay(500)
        return Result.success(
            listOf(
                Concession("1", "Pepsi lớn", 37000.0, ImageUrls.PEPSI_IMAGE, ConcessionType.DRINK),
                Concession("2", "Fanta lớn", 37000.0, ImageUrls.FANTA_IMAGE, ConcessionType.DRINK),
                Concession("3", "7Up lớn", 37000.0, ImageUrls.SEVEN_UP_IMAGE, ConcessionType.DRINK),
                Concession("4", "Bắp thường", 53000.0, ImageUrls.POPCORN_REGULAR, ConcessionType.POPCORN),
                Concession("5", "Bắp caramel", 57000.0, ImageUrls.POPCORN_CARAMEL, ConcessionType.POPCORN)
            )
        )
    }
}
