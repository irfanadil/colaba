package com.rnsoft.colabademo

import com.google.gson.annotations.SerializedName

data class OtpSentResponse(
    val code: String?,
    @SerializedName("data") val otpData: OtpData?,
    val message: String?,
    val status: String?
)

data class OtpData(
    val attemptsCount: Int? = 0,
    val phoneNumber: String?,
    val hasCompleted: Boolean?,
    val remainingTimeoutInSeconds: Int?,
    val lastSendAt: String?,
    val lastTwoFaCreatedOnUtc:String?,
    val twoFaMaxAttemptsCoolTimeInMinutes:Int?,
    val twoFaMaxAttemptsCoolTimeInSeconds:Int?=0



)