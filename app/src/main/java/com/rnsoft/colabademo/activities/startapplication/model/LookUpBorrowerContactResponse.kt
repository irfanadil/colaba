package com.rnsoft.colabademo

import com.google.gson.annotations.SerializedName

data class LookUpBorrowerContactResponse(
    val code: String,
    @SerializedName("data") val borrowerData: BorrowerExistData? = null,
    val message: String,
    val status: String
)


data class BorrowerExistData(
    val contactId: Int,
    val emailAddress: String,
    val firstName: String,
    val lastName: String,
    val midleName: Any,
    val mobileNumber: String
)