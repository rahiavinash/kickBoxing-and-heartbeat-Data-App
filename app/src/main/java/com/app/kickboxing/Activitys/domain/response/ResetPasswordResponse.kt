package com.app.kickboxing.Activitys.domain.response

data class ResetPasswordResponse (
    val success: Boolean,
    val message: String,
    val data: RegisterTokenData
)
