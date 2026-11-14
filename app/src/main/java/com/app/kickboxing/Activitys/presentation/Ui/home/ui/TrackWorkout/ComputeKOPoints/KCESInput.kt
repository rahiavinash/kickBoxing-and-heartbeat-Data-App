package com.app.kickboxing.Activitys.presentation.Ui.home.ui.TrackWorkout.ComputeKOPoints

data class KCESInput(
    val age: Int,
    val weight: Double,
    val gender: Gender,
    val averageHeartRate: Double,
    val totalPunches: Int,
    val averageGForce: Double,
    val zones: List<Double>,
    val useAlternateHR: Boolean
)
