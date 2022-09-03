package com.rnsoft.colabademo

import com.google.gson.annotations.SerializedName


data class LoanOfficerApiResponse(
    val code: String,
    @SerializedName("data") val loData: LoanOfficerData? = null,
    val message: Any,
    val status: String
)

data class LoanOfficerData(
    val roles: ArrayList<Role>
)

data class Role(
    val mcus: ArrayList<Mcu>,
    val roleId: Int,
    val roleName: String
)

data class Mcu(
    val branchId: Int,
    val branchName: String,
    val employeeId: Int,
    val fullName: String,
    val profileimageurl: String,
    val tenantCode: String,
    val userId: Int,
    val userName: String
)