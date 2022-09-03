package com.rnsoft.colabademo

import com.google.gson.annotations.SerializedName

data class LogoutResponse(
    val code: String?,
    @SerializedName("data") val logout: Data?,
    val message: String?,
    val status: String?
)