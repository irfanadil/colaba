package com.rnsoft.colabademo

import com.google.gson.annotations.SerializedName

/**
 * Created by Anita Kiran on 11/1/2021.
 */
data class RetirementAccountResponse(
    val code: String?,
    @SerializedName("data") val retirementAccountData: RetirementAccountData?,
    val message: String?,
    val status: String?
)

data class RetirementAccountData(
    val accountNumber: String?,
    val borrowerId: Int?,
    val id: Int?,
    val institutionName: String?,
    val loanApplicationId: Int?,
    val value: Double?
)

