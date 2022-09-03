package com.rnsoft.colabademo

import com.google.gson.annotations.SerializedName

data class SelfEmploymentResponse(
    val code: String?,
    @SerializedName("data")
    val selfEmploymentData: SelfEmploymentData?,
    val message: String?,
    val status: String?
)

data class SelfEmploymentData(
    @SerializedName("address")
    val businessAddress: AddressData?,
    val annualIncome: Double?,
    val borrowerId: Int?,
    val businessName: String?,
    val businessPhone: String?,
    val id: Int?,
    val jobTitle: String?,
    val loanApplicationId: Int?,
    val startDate: String?
)

data class SelfEmploymentAddress(
    val city: String?,
    val countryId: Int?,
    val countryName: Int?,
    val countyId: Int?,
    val countyName: String?,
    val stateId: Int?,
    val stateName: String?,
    val street: String?,
    val unit: String?,
    val zipCode: String?
)
