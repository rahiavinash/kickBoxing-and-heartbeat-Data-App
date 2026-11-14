package com.app.kickboxing.Activitys.domain.response

data class CancelWorkoutResponse (
    val success: Boolean,
    val message: String,
    val data: CancelData?

)

data class CancelData(

    var id : String? = null,
    var reservation : String? = null,
    var user : String? = null,
    var startTime : String? = null,
    var endTime : String? = null


)