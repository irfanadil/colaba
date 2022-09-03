package com.rnsoft.colabademo
import com.google.gson.annotations.SerializedName

data class NotificationItem(
    val id: Int?,
    val payload: Payload?,
    var status: String?,
    val notificationName: String ="",
    val notificationTime: String?,
    val notificationActive:Boolean = false,
    val isContent:Boolean = true,
    var isShowMenu:Boolean = false
)

data class Payload(
    @SerializedName("data") val payloadData : PayloadData?,
    val meta: Meta?
)

data class PayloadData(
    val address: String?,
    val city: String?,
    val dateTime: String?,
    val loanApplicationId: String?,
    val name: String?,
    val notificationType: String?,
    val state: String?,
    val unitNumber: String?,
    val zipCode: String?
)

data class Meta(
    val link: String?
)