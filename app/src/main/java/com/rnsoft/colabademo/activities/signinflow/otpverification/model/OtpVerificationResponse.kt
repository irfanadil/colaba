package com.rnsoft.colabademo

import com.google.gson.annotations.SerializedName


data class OtpVerificationResponse(
    val code: String?,
    @SerializedName("data") val data : Data?,
    val message: String?,
    val status: String?
)

data class OtpVerificationData(
    val isLoggedIn: Boolean,
    val refreshToken: String,
    val refreshTokenValidTo: String,
    val token: String,
    val userName: String,
    val userProfileId: Int,
    val validFrom: String,
    val validTo: String,
    val tokenType: Int,
    val tokenTypeName: String
)