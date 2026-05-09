package com.example.cah_cinema.presentation.booking

import com.example.cah_cinema.domain.model.Concession
import com.example.cah_cinema.domain.model.ConcessionType
import com.example.cah_cinema.domain.repository.ConcessionRepository
import kotlinx.coroutines.delay

class ConcessionRepositoryImpl : ConcessionRepository {
    override suspend fun getConcessions(): Result<List<Concession>> {
        // Giả lập độ trễ mạng
        delay(500)
        return Result.success(
            listOf(
                Concession("1", "Pepsi lớn", 37000.0, "https://files.betacinemas.vn/files/media/images/2023/10/11/ly-pepsi-105151-111023-42.png", ConcessionType.DRINK),
                Concession("2", "Fanta lớn", 37000.0, "https://files.betacinemas.vn/files/media/images/2023/10/11/ly-fanta-105234-111023-56.png", ConcessionType.DRINK),
                Concession("3", "7Up lớn", 37000.0, "https://files.betacinemas.vn/files/media/images/2023/10/11/ly-7up-105214-111023-48.png", ConcessionType.DRINK),
                Concession("4", "Bắp thường", 53000.0, "https://files.betacinemas.vn/files/media/images/2023/10/11/bap-ngot-105253-111023-14.png", ConcessionType.POPCORN),
                Concession("5", "Bắp caramel", 57000.0, "https://files.betacinemas.vn/files/media/images/2023/10/11/bap-phomai-105314-111023-95.png", ConcessionType.POPCORN)
            )
        )
    }
}
