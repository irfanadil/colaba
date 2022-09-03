package com.rnsoft.colabademo

/**
 * Created by Anita Kiran on 11/9/2021.
 */
data class UpdateLoanRefinanceModel(
    val cashOutAmount: Double?,
    val downPayment: Double?,
    val propertyValue : Double?,
    val loanApplicationId: Int?,
    val loanGoalId: Int?,
    val loanPurposeId: Int?,
    val loanPayment: Double? = null
)
