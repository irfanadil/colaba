package com.rnsoft.colabademo

import com.google.gson.annotations.SerializedName

data class LoanInfoDetailsModel(
    val code: String,
    @SerializedName("data")val data: LoanInfoData?,
    val message: String?,
    val status: String
)