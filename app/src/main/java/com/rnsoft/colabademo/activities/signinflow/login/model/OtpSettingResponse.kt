package com.rnsoft.colabademo

import com.google.gson.annotations.SerializedName

data class OtpSettingResponse(
    val code: String,
    @SerializedName("data") val otpSettingData: OtpSettingData?,
    val message: String?,
    val status: String?
)

data class OtpSettingData(
    val maxTwoFaSendAllowed: Int?,
    val twoFaResendCoolTimeInMinutes: Int?
)

