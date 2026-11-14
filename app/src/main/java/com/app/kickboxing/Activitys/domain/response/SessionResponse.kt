
package com.app.kickboxing.Activitys.domain.response

import com.google.gson.annotations.SerializedName

//data class SessionDetailResponse (
//    val success: Boolean,
//    val message: String,
// val data: SessionDetail
//)


data class SessionResponse(
    @SerializedName("success")
    val success: Boolean? = null,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("data")
    val data: SessionDataWrapper? = null
)

data class SessionDataWrapper(
    @SerializedName("list")
    val list: List<SessionData>? = null,
    @SerializedName("pagination")
    val pagination: Pagination? = null
)

data class SessionData(
    @SerializedName("_id")
    val id: String? = null,
    @SerializedName("user")
    val user: String? = null,
    @SerializedName("startTime")
    val startTime: String? = null,
    @SerializedName("createdAt")
    val createdAt: String? = null,
    @SerializedName("updatedAt")
    val updatedAt: String? = null,
    @SerializedName("__v")
    val version: Int? = null,
    @SerializedName("averageGForce")
    val averageGForce: Double? = 0.0,
    @SerializedName("averageHeartRate")
    val averageHeartRate: Double? = 0.0,
    @SerializedName("averagePunchingDistance")
    val averagePunchingDistance: Double? = 0.0,
    @SerializedName("averagePunchingSpeed")
    val averagePunchingSpeed: Double? = 0.0,
    @SerializedName("endTime")
    val endTime: String? = null,
    @SerializedName("maxGForce")
    val maxGForce: Double? = 0.0,
    @SerializedName("maxPunchingSpeed")
    val maxPunchingSpeed: Double? = 0.0,
    @SerializedName("score")
    val score: Double? = 0.0,
    @SerializedName("totalPunches")
    val totalPunches: Float? = 0f
)

data class Pagination(
    @SerializedName("totalCount")
    val totalCount: Int? = null,
    @SerializedName("nextPage")
    val nextPage: Int? = null,
    @SerializedName("prevPage")
    val prevPage: Int? = null,
    @SerializedName("currentPage")
    val currentPage: Int? = null
)

data class SessionDetailResponse(
    @SerializedName("success")
    val success: Boolean? = null,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("data")
    val data: SessionData? = null
)
