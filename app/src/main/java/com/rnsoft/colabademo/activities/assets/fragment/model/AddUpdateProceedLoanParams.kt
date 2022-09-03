package com.rnsoft.colabademo

data class AddUpdateProceedLoanParams(
    val BorrowerId: Int,
    val LoanApplicationId: Int,
    val AssetCategoryId: Int,
    val AssetTypeId: Int,

     val AssetValue: Int?=null,
    val BorrowerAssetId: Int?=null,
    val CollateralAssetDescription: String? = null,
    val ColletralAssetTypeId: Int?=null,
    val SecuredByColletral: Boolean?=null
)