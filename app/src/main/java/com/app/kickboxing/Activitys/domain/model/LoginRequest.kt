package com.app.kickboxing.Activitys.domain.model

data class LoginRequest(
    val email: String? = null,
    val password: String? = null,
    val deviceType: String? = null,
    val deviceToken: String? = null,
    val fcmToken: String? = null,
)


