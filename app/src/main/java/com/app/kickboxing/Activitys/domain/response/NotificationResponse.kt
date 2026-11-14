package com.app.kickboxing.Activitys.domain.response

import com.google.gson.annotations.SerializedName

data class NotificationResponse(
    @SerializedName("success")
    val success: Boolean? = null,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("data")
    val data: NotificationDataWrapper? = null

)

data class NotificationDataWrapper(
    val list: List<NotificationData>?,
    val pagination: Pagination?
)

data class NotificationData (
    @SerializedName("_id")
    val _id: String? = null,
    @SerializedName("__v")
    val __v: String? = null,
    @SerializedName("sender")
    val sender: String? = null,
    @SerializedName("receiver")
    val receiver: String? = null,
    @SerializedName("type")
    val type: String? = null,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("seen")
    val seen: Boolean? = false,
    @SerializedName("createdAt")
    val createdAt: String? = "",
    @SerializedName("updatedAt")
    val updatedAt: String? = "",

)


