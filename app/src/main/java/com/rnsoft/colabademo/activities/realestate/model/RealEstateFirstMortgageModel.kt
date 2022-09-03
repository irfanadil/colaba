package com.rnsoft.colabademo

import com.google.gson.annotations.SerializedName

/**
 * Created by Anita Kiran on 10/15/2021.
 */

data class RealEstateFirstMortgageModel(
    val code: String,
    @SerializedName("data")
    val data: FirstMortgageData?,
    val message: String?,
    val status: String
)

data class FirstMortgageData(
    val firstMortgagePayment: Double?,
    val floodInsurance: Double?,
    val floodInsuranceIncludeinPayment: Boolean?,
    val helocCreditLimit: Double?,
    val homeOwnerInsurance: Double?,
    val homeOwnerInsuranceIncludeinPayment: Boolean?,
    val id: Int?,
    val isHeloc: Boolean?,
    val loanApplicationId: Int?,
    val paidAtClosing: Boolean?,
    val propertyTax: Double?,
    val propertyTaxesIncludeinPayment: Boolean?,
    val unpaidFirstMortgagePayment: Double?
)
