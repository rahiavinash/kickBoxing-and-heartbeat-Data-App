package com.app.kickboxing.Activitys.domain.response

data class RegisterResponse(
    val success: Boolean,
    val message: String,
    val data: RegisterTokenData
)
data class RegisterTokenData(
    val accessToken: String,
    val refreshToken: String
)

