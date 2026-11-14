package com.app.kickboxing.Activitys.domain.response

import android.net.Uri
import kotlinx.serialization.Serializable

data class CreateProfileResponse (
    val success: Boolean,
    val message: String,
    val data: RegisterTokenData
)

data class GetProfileResponse (
    val success: Boolean,
    val message: String,
    val data: ProfileData
)

data class ProfileData(
    val _id: String? = null,
    val email: String? = null,
    val classAttendenceGoal: Int? = null,
    val completeProfile: Boolean? = null,
    val gender: String? = null,
    val height: Double? = null,
    val pointAverageGoal: Int? = null,
    val profile: String? = null,
    val punch : String? = null,
    val punchAverageGoal : Int? = null,
    val weight: Int? = null,
    val weightGoals: Int? = null,
    val DOB: String? = null,
    val fullName: String? = null,
    val userName: String? = null
)

