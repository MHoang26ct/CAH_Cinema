package com.example.cah_cinema.data.mapper

import com.example.cah_cinema.data.model.UserInfo
import com.example.cah_cinema.domain.model.User

fun UserInfo.toDomain(): User {
    return User(
        id = this.userId,
        name = this.name,
        email = this.email,
        phone = this.phone,
        avatarUrl = this.avatarUrl,
        role = this.role,
        totalPaid = this.totalPaid,
        totalPoint = this.totalPoint,
        rankLevel = this.rankLevel
    )
}
