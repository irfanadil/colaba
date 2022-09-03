package com.rnsoft.colabademo.activities.assets.fragment.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Anita Kiran on 11/1/2021.
 */
data class AssetsRealEstateResponse(
    val code: String?,
    @SerializedName("data")
    val data: AssetRealEstateData?,
    val message: String?,
    val status: String?
)


data class AssetRealEstateData(
    val assetTypeCategoryName: String?,
    val assetTypeId: Int?,
    val asstTypeName: String?,
    val description: String?,
    val id: Int?,
    val value: Double?
)
