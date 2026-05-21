package com.example.cah_cinema.data.mapper

import com.example.cah_cinema.data.model.CinemaItem
import com.example.cah_cinema.domain.model.Cinema

fun CinemaItem.toDomain(): Cinema {
    return Cinema(
        id = this.id,
        name = this.name,
        address = this.address,
        imageUrl = this.imageUrl,
        hotline = this.hotline
    )
}
