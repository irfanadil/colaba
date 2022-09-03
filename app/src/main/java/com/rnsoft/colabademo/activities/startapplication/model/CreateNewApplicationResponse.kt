package com.rnsoft.colabademo
import com.google.gson.annotations.SerializedName

data class CreateNewApplicationResponse(
    val code: String,
    @SerializedName("data") val createNewAppData: CreateNewAppData? = null,
    val message: Any,
    val status: String
)

data class CreateNewAppData(
    val borrowerId: Int,
    val branchId: Int,
    val contactId: Int,
    val emailAddress: String,
    val firstName: String,
    val lastName: String,
    val loanApplicationId: Int,
    val loanGoal: Int,
    val loanOfficerUserId: Int,
    val loanPurpose: Int,
    val mobileNumber: String
)