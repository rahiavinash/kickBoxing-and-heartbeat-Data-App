package com.app.kickboxing.Activitys.domain.response

import com.google.gson.annotations.SerializedName

data class LogoutResponse(

    @SerializedName("success")
    val success: Boolean? = null,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("data")
    val data: Any? = null
)

