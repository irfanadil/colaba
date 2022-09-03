package com.rnsoft.colabademo

import com.google.gson.annotations.SerializedName

data class BankAddUpdateParams(
    val AssetTypeId: Int,
    val LoanApplicationId: Int,
    val BorrowerId: Int,
    @SerializedName("id") val bankUniqueId: Int?,
    val AccountNumber: String? = null,
    val Balance: Int? = null,
    val InstitutionName: String? = null


)