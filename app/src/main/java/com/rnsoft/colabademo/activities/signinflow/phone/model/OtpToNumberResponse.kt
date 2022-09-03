package com.rnsoft.colabademo

import com.google.gson.annotations.SerializedName

data class OtpToNumberResponse(
    val code: String?,
    @SerializedName("data") val otpData: OtpData2?,
    val message: String?,
    val status: String?
)

data class OtpData2(
    val attemptsCount: Int,
    val phoneNumber: String
)