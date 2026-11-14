package com.app.kickboxing.Activitys.domain.model

import com.google.gson.annotations.SerializedName

data class User(
    var name : String
)

data class HomeRequest(
    @SerializedName("type")
    val type: String? = null
)

data class SessionRequest(
    @SerializedName("limit")
    val limit : Int? = null,

    @SerializedName("offset")
    val offset : Int? = null,

    @SerializedName("order")
    val order : Int? = null,

    @SerializedName("sort")
    val sort : String? = null,


    @SerializedName("type")
    val type : String? = null,


    @SerializedName("startDate")
    val startDate : String? = null,

    @SerializedName("endDate")
    val endDate : String? = null


)

data class SessionDetailRequest(
    @SerializedName("session")
    val session  : String? = null,
)

data class LeaderboardDetailRequest(
    @SerializedName("limit")
    val limit : Int? = null,

    @SerializedName("offset")
    val offset : Int? = null,

    @SerializedName("type")
    val type : String? = null,

//    @SerializedName("order")
//    val order : Int? = null,
//
//    @SerializedName("sort")
//    val sort : String? = null
)

data class ScheduledWorkoutRequest(
    @SerializedName("limit")
    val limit : Int? = null,

    @SerializedName("offset")
    val offset : Int? = null,

)

data class JoinWorkoutRequest(
    @SerializedName("reservation")
    val id: String? = null
)

data class CancelWorkoutRequest(
    @SerializedName("reservation")
    val id: String? = null
)

