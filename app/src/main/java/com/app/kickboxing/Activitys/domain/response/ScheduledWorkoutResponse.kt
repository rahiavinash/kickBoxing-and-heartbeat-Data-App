package com.app.kickboxing.Activitys.domain.response

import com.google.gson.annotations.SerializedName

data class ScheduledWorkoutResponse(
    @SerializedName("success")
    val success: Boolean? = null,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("data")
    val data: WorkoutDataWrapper? = null
)
data class WorkoutDataWrapper(
    @SerializedName("list")
    val list: List<WorkoutData>? = null,
    @SerializedName("pagination")
    val pagination: Pagination? = null
)

data class WorkoutData(
    @SerializedName("_id")
    val id: String? = null,
    @SerializedName("__v")
    val version: Int? = null,
    @SerializedName("candidates")
    val candidates: Int? = null,
    @SerializedName("classType")
    val classType: String? = null,
    @SerializedName("createdAt")
    val createdAt: String? = null,
    @SerializedName("location")
    val location: String? = null,
    @SerializedName("startTime")
    val startTime: String? = null,
    @SerializedName("endTime")
    val endTime: String? = null,
    @SerializedName("updatedAt")
    val updatedAt: String? = null,
    @SerializedName("joined")
    val joined : Boolean? = null,
    @SerializedName("gym")
    val gym : String ?=null,
    @SerializedName("focus")
    val focus :String ?=null,
    @SerializedName("cords")
    val cords: List<Double>? = null,
)





