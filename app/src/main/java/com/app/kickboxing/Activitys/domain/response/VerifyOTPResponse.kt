package com.app.kickboxing.Activitys.domain.response

data class VerifyOTPResponse (
    val success: Boolean,
    val message: String,
    val data: User
)

data class User(
    var user : String
)