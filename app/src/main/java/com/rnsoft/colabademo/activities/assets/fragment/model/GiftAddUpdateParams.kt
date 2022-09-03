package com.rnsoft.colabademo

data class GiftAddUpdateParams(
    val GiftSourceId: Int,
    val BorrowerId: Int,
    val LoanApplicationId: Int,
    val Description: String? = null,
    val AssetTypeId: Int?= null,
    val Id: Int? = null,
    val IsDeposited: Boolean?=null,
    val Value: Int?= null,
    val valueDate: String?= null
)