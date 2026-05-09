package com.example.cah_cinema.domain.usecase

import com.example.cah_cinema.domain.model.Concession
import com.example.cah_cinema.domain.repository.ConcessionRepository

class GetConcessionsUseCase(private val repository: ConcessionRepository) {
    suspend operator fun invoke(): Result<List<Concession>> {
        return repository.getConcessions()
    }
}
