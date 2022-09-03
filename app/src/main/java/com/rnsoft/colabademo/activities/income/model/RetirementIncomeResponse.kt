package com.rnsoft.colabademo

import com.google.gson.annotations.SerializedName

data class RetirementIncomeResponse(
    val code: String?,
    @SerializedName("data")
    val retirementIncomeData: RetirementIncomeData?,
    val message: String?,
    val status: String?
)

data class RetirementIncomeData(
    val loanApplicationId: Int?,
    val borrowerId: Int?,
    val incomeInfoId: Int?,
    val incomeTypeId: Int?,
    val description: String?,
    val employerName: String?,
    val monthlyBaseIncome: Double?
)
