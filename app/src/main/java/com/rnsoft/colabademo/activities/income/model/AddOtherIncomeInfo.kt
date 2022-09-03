package com.rnsoft.colabademo

/**
 * Created by Anita Kiran on 11/15/2021.
 */
data class AddOtherIncomeInfo(
    val description: String? = null,
    //val fieldInfo: String?,
    //val incomeGroupId: Int?,
    //val incomeGroupName: String?,
    val borrowerId: Int?,
    val incomeInfoId: Int?,
    val incomeTypeId: Int?,
    val loanApplicationId: Int?,
    val monthlyBaseIncome: Double? = null,
    val annualBaseIncome: Double? = null

    )
