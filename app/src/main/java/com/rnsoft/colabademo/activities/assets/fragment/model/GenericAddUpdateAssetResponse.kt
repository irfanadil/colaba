package com.rnsoft.colabademo

import com.google.gson.annotations.SerializedName

data class GenericAddUpdateAssetResponse(
    val code: Any?= null,
    @SerializedName("data") val assetUniqueId: Int? = null,
    val message: String? = null,
    val status: String? = null
)