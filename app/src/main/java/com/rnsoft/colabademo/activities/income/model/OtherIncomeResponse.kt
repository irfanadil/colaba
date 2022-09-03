package com.rnsoft.colabademo

import com.google.gson.annotations.SerializedName

data class OtherIncomeResponse(
    val code: String?,
    @SerializedName("data")
    val otherIncomeData: OtherIncomeData?,
    val message: String?,
    val status: String?
)

data class OtherIncomeData(
    val annualBaseIncome: Double?,
    val description: String?,
    val fieldInfo: String?,
    val incomeGroupId: Int?,
    val incomeGroupName: String?,
    val incomeInfoId: Int?,
    val incomeTypeId: Int?,
    val incomeTypeName: String?,
    val monthlyBaseIncome: Double?
)
