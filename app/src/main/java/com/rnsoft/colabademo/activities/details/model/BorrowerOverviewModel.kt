package com.rnsoft.colabademo

import com.google.gson.annotations.SerializedName

data class BorrowerOverviewModel(
    @SerializedName("address") val webBorrowerAddress : WebBorrowerAddress?=null,
    @SerializedName("borrowers") val coBorrowers : ArrayList<CoBorrower>?,
    val cellPhone: String? ="",
    val downPayment: Double?,
    val email: String? ="",
    val loanAmount: Double?,
    val loanNumber: String? = "",
    val loanPurpose: String? = "",
    val milestone: String? = "",
    val milestoneId: Int?,
    val postedOn: String? = "",
    val propertyType: String? = "",
    val propertyValue: Double?,
    val propertyUsage: String? = "",
    val loanGoal: String? = ""
)

data class CoBorrower(
    val firstName: String?= "",
    val lastName: String? = "",
    val middleName:  String? = "",
    val ownType:String? = "",
    val ownTypeId: Int?
)

data class WebBorrowerAddress(
    val city: String? = "",
    val countryId: Int?,
    val countryName: String? = "",
    val countyId: Int?,
    val countyName: String? = "",
    val stateId: Int?,
    val stateName: String? = "",
    val street: String? = "",
    val unit: String? = "",
    val zipCode: String? = ""
)