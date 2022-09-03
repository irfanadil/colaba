package com.rnsoft.colabademo

data class LoanInfoData(
    val cashOutAmount: Double? = null,
    val downPayment: Double? = null,
    val expectedClosingDate: String? = null,
    val loanApplicationId: Int? = null,
    val loanGoalId: Int? = null,
    val loanGoalName: String? = null,
    val loanPayment: Double? = null,
    val loanPurposeDescription: String? = null,
    val loanPurposeId: Int? = null,
    val propertyValue: Double? = null
)