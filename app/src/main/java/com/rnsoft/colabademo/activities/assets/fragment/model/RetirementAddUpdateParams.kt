package com.rnsoft.colabademo

data class RetirementAddUpdateParams(
    val BorrowerId: Int,
    val LoanApplicationId: Int,
    val AccountNumber: String,
    val InstitutionName: String,
    val Value: Int,
    val Id: Int?=null,

)