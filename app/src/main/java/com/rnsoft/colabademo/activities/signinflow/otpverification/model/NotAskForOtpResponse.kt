package com.rnsoft.colabademo

import com.google.gson.annotations.SerializedName


data class NotAskForOtpResponse(
    val code: String?,
    @SerializedName("data") val notAskForData : NotAskForData?,
    val message: String?,
    val status: String?
)

data class NotAskForData(val dontAskTwoFaIdentifier:String?)