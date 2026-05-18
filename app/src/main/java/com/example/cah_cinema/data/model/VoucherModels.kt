package com.example.cah_cinema.data.model

import com.google.gson.annotations.SerializedName

data class UserVoucherResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: List<VoucherItem>
)

data class SliceResponse<T>(
    @SerializedName("content") val content: List<T>,
    @SerializedName("pageable") val pageable: PageableInfo,
    @SerializedName("first") val first: Boolean,
    @SerializedName("last") val last: Boolean,
    @SerializedName("size") val size: Int,
    @SerializedName("number") val number: Int,
    @SerializedName("numberOfElements") val numberOfElements: Int,
    @SerializedName("empty") val empty: Boolean
)

data class PageableInfo(
    @SerializedName("pageNumber") val pageNumber: Int,
    @SerializedName("pageSize") val pageSize: Int
)

data class VoucherItem(
    @SerializedName("voucherId") val id: Long,
    @SerializedName("code") val code: String,
    @SerializedName("type") val type: String,
    @SerializedName("value") val value: Double,
    @SerializedName("minOrderValue") val minOrderValue: Double?,
    @SerializedName("maxDiscount") val maxDiscount: Double?,
    @SerializedName("quantity") val quantity: Int? = null,
    @SerializedName("usedCount") val usedCount: Int? = null,
    @SerializedName("startAt") val startAt: String? = null,
    @SerializedName("expiredAt") val expiredAt: String,
    @SerializedName("isActive") val isActive: Boolean? = true,
    @SerializedName("isDeleted") val isDeleted: Boolean? = false
)
