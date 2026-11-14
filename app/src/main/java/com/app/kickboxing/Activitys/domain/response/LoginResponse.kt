package com.app.kickboxing.Activitys.domain.response

data class LoginResponse(
    val success: Boolean? = null,
    var message: String? = null,
    var error: Int? = null,
    val data: TokenData? = null
)

data class TokenData(
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val completeProfile: Boolean? = null,
)