package com.rnsoft.colabademo

import com.google.gson.annotations.SerializedName

data class MilitaryIncomeResponse(
    val code: String?,
    @SerializedName("data")
    val militaryIncomeData: MilitaryIncomeData?,
    val message: String?,
    val status: String?
)


data class MilitaryIncomeData(
    val loanApplicationId: Int?,
    val id: Int?,
    val borrowerId: Int?,
    val address: AddressData?,
    val employerName: String?,
    val jobTitle: String?,
    val militaryEntitlements: Double?,
    val monthlyBaseSalary: Double?,
    val startDate: String?,
    val yearsInProfession: Int?
)