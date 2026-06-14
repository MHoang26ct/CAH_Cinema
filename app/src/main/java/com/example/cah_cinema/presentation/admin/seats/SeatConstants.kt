package com.example.cah_cinema.presentation.admin.seats

/**
 * Loại ô trong grid thiết kế sơ đồ ghế.
 * Tọa độ backend dùng x.5 cho aisle, x.0 cho ghế thật.
 *
 * seatTypeId mapping:
 *   1L = REGULAR (ghế thường)
 *   2L = VIP
 *   3L = COUPLE (ghế đôi - phải đặt thành cặp liên tiếp)
 *   4L = AISLE  (lối đi - không gửi lên backend, chỉ dùng để tính tọa độ x.5)
 *   0L = EMPTY  (ô trống, không có ghế)
 */
const val TYPE_EMPTY = 0L
const val TYPE_REGULAR = 1L
const val TYPE_VIP = 2L
const val TYPE_COUPLE = 3L
const val TYPE_AISLE = 4L
