package com.app.kickboxing.Activitys.domain.response

data class LeaderBoardResponse(
    val success: Boolean,
    val message: String,
    val data: LeaderListData

)
data class LeaderListData(
    val list: List<LeaderItem>,
    val user: LeaderItem,
    val pagination: Pagination
)

data class LeaderItem(
    val _id: String,
    val user: String,
    val startTime: String,
    val createdAt: String,
    val updatedAt: String,
    val __v: Int,
    val averageGForce: Double,
    val averageHeartRate: Double,
    val averagePunchingDistance: Double,
    val averagePunchingSpeed: Double,
    val endTime: String,
    val maxGForce: Double,
    val maxPunchingSpeed: Double,
    val score: Double,
    val totalPunches: Float,
    val userDetails: UserDetails,
    val rank: Int
)

data class UserDetails(
    val _id: String,
    val profile: String?=null,
    val fullName: String,
    val userName: String
)
