package com.rnsoft.colabademo

import com.google.gson.annotations.SerializedName

data class ProceedFromLoanResponse(
    val code: String?,
    @SerializedName("data")
    val data: ProceesFromLoanData?,
    val message: String?,
    val status: String?
)

data class ProceesFromLoanData(
    val assetTypeCategoryName: String?,
    val assetTypeId: Int?,
    val asstTypeName: String?,
    val borrowerId: Int?,
    val collateralAssetName: String?,
    val collateralAssetOtherDescription: String?,
    val collateralAssetTypeId: Int?,
    val description: String?,
    val id: Int?,
    val securedByCollateral: Boolean?,
    val value: Double?
)