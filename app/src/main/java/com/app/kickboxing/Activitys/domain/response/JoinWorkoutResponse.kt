package com.app.kickboxing.Activitys.domain.response

import com.google.gson.annotations.SerializedName

data class JoinWorkoutResponse(
    val success: Boolean,
    val message: String,
    val data: JoinData
)

data class JoinData(
    @SerializedName("_id")
    val id: String,
    val candidates: Int,
    val classType: String,
    val createdAt: String,
    val endTime: String,
    val location: String,
    val startTime: String
)
