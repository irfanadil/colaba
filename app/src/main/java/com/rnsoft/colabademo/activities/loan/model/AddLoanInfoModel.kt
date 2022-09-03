package com.rnsoft.colabademo

data class AddLoanInfoModel(
    val cashOutAmount: Int?,
    val downPayment: Double?,
    val expectedClosingDate: String?,
    val loanApplicationId: Int,
    val loanGoalId: Int?,
    val loanPurposeId: Int,
    val propertyValue: Double?,
    val loanPayment: Double?
)