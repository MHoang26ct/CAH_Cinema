package com.example.cah_cinema.data.model

import com.google.gson.annotations.SerializedName

data class CommentItem(
    @SerializedName("commentId", alternate = ["id"]) val id: Long,
    @SerializedName("userId") val userId: Long,
    @SerializedName("userName") val userName: String,
    @SerializedName("userAvatar") val userAvatar: String?,
    @SerializedName("content") val content: String,
    @SerializedName("createdAt") val createdAt: String
)

data class CreateCommentRequest(
    @SerializedName("content") val content: String
)
