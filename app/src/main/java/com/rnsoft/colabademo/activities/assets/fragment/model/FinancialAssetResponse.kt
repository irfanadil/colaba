package com.rnsoft.colabademo

import com.google.gson.annotations.SerializedName

data class FinancialAssetResponse(
    val code: String?,
    @SerializedName("data")
    val financialAssetData: FinancialAssetData?,
    val message: String?,
    val status: String?
)

data class FinancialAssetData(
    val id: Int?,
    val assetTypeId: Int?,
    val institutionName: String?,
    val accountNumber: String?,
    val balance: Double?,
    val loanApplicationId: Int?,
    val borrowerId: Int?,
)
