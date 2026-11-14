package com.app.kickboxing.Activitys.presentation.Ui.home.ui.TrackWorkout.UseCase

import android.util.Log

fun getHeartRateProgress(heartRate: Int): Float {
    Log.e("145464646456456", heartRate.toString())
    return when {
        heartRate == 0 -> 0.0f
        heartRate in 1..10 -> 0.1f
        heartRate in 10..20 -> 0.2f
        heartRate in 20..30 -> 0.3f
        heartRate in 40..50 -> 0.5f
        heartRate in 50..60 -> 0.6f
        heartRate in 60..70 -> 0.7f
        heartRate in 70..80 -> 0.8f
        heartRate in 80..100 -> 0.85f
        heartRate in 100..120 -> 0.9f
        else -> 1.0f
    }

}