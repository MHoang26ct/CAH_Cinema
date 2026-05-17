package com.example.cah_cinema.data.model

import com.google.gson.annotations.SerializedName

data class UserVoucherResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: List<VoucherItem>
)

data class VoucherItem(
    @SerializedName("voucherId") val id: Long,
    @SerializedName("code") val code: String,
    @SerializedName("type") val type: String,
    @SerializedName("value") val value: Double,
    @SerializedName("minOrderValue") val minOrderValue: Double,
    @SerializedName("maxDiscount") val maxDiscount: Double?,
    @SerializedName("expiredAt") val expiredAt: String
)
