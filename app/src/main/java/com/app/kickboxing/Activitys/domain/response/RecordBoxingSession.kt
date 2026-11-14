package com.app.kickboxing.Activitys.domain.response

data class RecordBoxingSession(
    val session: String,
    val totalPunches: Int,
    val punchingType: String,
    val punchingTime: Long,
    val withdrawingTime: Long,
    val gForce: Float,
    val punchingSpeed: Float,
    val punchingDistance: Float,
    val heartRate: Int
)