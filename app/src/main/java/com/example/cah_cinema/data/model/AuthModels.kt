package com.example.cah_cinema.data.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class LoginData(
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("refreshToken") val refreshToken: String,
    @SerializedName("user") val user: UserInfo? = null
)

data class RegisterRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("name") val name: String,
    @SerializedName("phone") val phone: String? = null
)

data class OtpRequest(
    @SerializedName("email") val email: String
)

data class OtpVerifyRequest(
    @SerializedName("email") val email: String,
    @SerializedName("otp") val otp: String
)

data class OtpVerifyResponse(
    @SerializedName("token") val token: String
)

data class ResetPasswordRequest(
    @SerializedName("email") val email: String,
    @SerializedName("newPassword") val newPassword: String,
    @SerializedName("resetToken") val resetToken: String
)

data class ChangePasswordRequest(
    @SerializedName("oldPassword") val oldPassword: String,
    @SerializedName("newPassword") val newPassword: String
)

data class RefreshTokenRequest(
    @SerializedName("refreshToken") val refreshToken: String
)

data class GoogleLoginRequest(
    @SerializedName("idToken") val idToken: String
)
