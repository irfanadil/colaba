package com.rnsoft.colabademo

import com.google.gson.annotations.SerializedName

/**
 * Created by Anita Kiran on 11/12/2021.
 */
data class PreviousEmploymentResponse(
    val code: String?,
    @SerializedName("data")
    val employmentData: PreviousEmploymentData?,
    val message: String?,
    val status: String?
)


data class PreviousEmploymentData(
    val loanApplicationId: Int?,
    val borrowerId: Int?,
    val employerAddress: AddressData?,
    val employmentInfo: PrevEmploymentInfo?,
    val wayOfIncome: WayOfIncomePrevious?
)


data class PrevEmploymentInfo(
    //val employedByFamilyOrParty: Boolean?,
    val employerName: String?,
    val employerPhoneNumber: String?,
    val startDate: String?,
    val endDate: String?,
    val hasOwnershipInterest: Boolean?,
    val incomeInfoId: Int?,
    val jobTitle: String?,
    val ownershipInterest: Double?,
    val yearsInProfession: Int?
)

data class WayOfIncomePrevious(
    val employerAnnualSalary: Double?
)
