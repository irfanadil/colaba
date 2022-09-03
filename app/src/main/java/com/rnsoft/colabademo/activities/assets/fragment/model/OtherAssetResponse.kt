package com.rnsoft.colabademo

import com.google.gson.annotations.SerializedName

data class OtherAssetResponse(
    val code: String?,
    @SerializedName("data") val otherAssetData: OtherAssetData?,
    val message: String?,
    val status: String?
)

data class OtherAssetData(
    val accountNumber: String?,
    val assetDescription: String?,
    val otherUniqueId: Int?,
    val assetTypeId: Int?,
    val assetTypeName: String?,
    val assetValue: Double?,
    val institutionName: String?
)

