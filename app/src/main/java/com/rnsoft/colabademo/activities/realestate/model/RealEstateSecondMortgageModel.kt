package com.rnsoft.colabademo

import com.google.gson.annotations.SerializedName

/**
 * Created by Anita Kiran on 10/15/2021.
 */
data class RealEstateSecondMortgageModel(
    val code: String,
    @SerializedName("data")
    val data: SecondMortgageData?,
    val message: String?,
    val status: String
)

data class SecondMortgageData(
    val helocCreditLimit: Double?,
    val id: Int?,
    val isHeloc: Boolean?,
    val loanApplicationId: Int?,
    val paidAtClosing: Boolean?,
    val secondMortgagePayment: Double?,
    val unpaidSecondMortgagePayment: Double?
)