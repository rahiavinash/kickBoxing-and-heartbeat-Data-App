package com.app.kickboxing.Activitys.domain.model

import android.net.Uri
import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @SerializedName("email")
    val email: String? = null,

    @SerializedName("password")
    val password: String? = null
)
data class SendOTPRequest(
    @SerializedName("email")
    val email: String? = null
)
data class VerifyOtpRequest(
    @SerializedName("email")
    val email: String? = null,

    @SerializedName("otp")
    val otp: Int? = null
)

data class ResetPasswordRequest(
    @SerializedName("user")
    val user: String? = null,

    @SerializedName("password")
    val password: String? = null
)



data class CreateProfileRequest(
    val userName: String? = null,
    val fullName: String? = null,
    val DOB : String? = null,
    val gender: String? = null,
    val height: Double? = null,
    val weight: Int? = null,
    val profile: String? = null,
    val userImageUri: Uri? = null,
    val punchAverageGoal: Int? = null,
    val weightGoals : Int? = null,
    val completeProfile: Boolean? = null,
)

data class NotificationRequest(

    @SerializedName("limit")
    val limit : Int? = null,

    @SerializedName("offset")
    val offset : Int? = null,

    )

