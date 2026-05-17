package com.example.cah_cinema.domain.repository

import com.example.cah_cinema.domain.model.Concession

interface ConcessionRepository {
    suspend fun getConcessions(): Result<List<Concession>>
}
