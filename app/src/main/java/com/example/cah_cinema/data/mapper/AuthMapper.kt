package com.example.cah_cinema.data.mapper

import com.example.cah_cinema.data.model.LoginData
import com.example.cah_cinema.domain.model.LoginResult

fun LoginData.toDomain(): LoginResult {
    return LoginResult(
        accessToken = this.accessToken,
        refreshToken = this.refreshToken,
        user = this.user!!.toDomain()
    )
}
