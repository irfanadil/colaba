package com.rnsoft.colabademo

import com.google.gson.annotations.SerializedName

data class TenantConfigurationResponse(
    val code: String,
    @SerializedName("data") val tenantData: TenantData,
    val message: Any?,
    val status: String
)

data class TenantData(
    val tenantTwoFaSetting: Int,
    val userTwoFaSetting: Boolean?= null
)