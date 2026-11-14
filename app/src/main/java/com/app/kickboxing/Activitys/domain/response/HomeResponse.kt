package com.app.kickboxing.Activitys.domain.response

data class HomeResponse (

    val success: Boolean,
    val message: String,
    val data: HomeData?
)


data class HomeData (
    var rank : Int? = 0,
    var averageTotalPunches : Double? = 0.0,
    var averageGForce : Double? = 0.0,
    var averageScore : Double? = 0.0,
    var previousAverageTotalPunches : Double? = 0.0,
    var previousAverageGForce : Double? = 0.0,
    var previousAverageScore : Double? = 0.0,
    var type : String? = ""
)