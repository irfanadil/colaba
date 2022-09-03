package com.rnsoft.colabademo

import com.google.gson.annotations.SerializedName


data class SkipTwoFactorResponse(
    val code: String,
    @SerializedName("data") var skipTwoFactorData: SkipTwoFactorData?,
    val message: String?,
    val status: String?
)

data class SkipTwoFactorData(
    val refreshToken: String,
    val refreshTokenValidTo: String,
    val token: String,
    val tokenType: Int,
    val tokenTypeName: String,
    val userName: String,
    val userProfileId: Int,
    val validFrom: String,
    val validTo: String
)