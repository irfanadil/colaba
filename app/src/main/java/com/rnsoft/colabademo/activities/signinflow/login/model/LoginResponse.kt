package com.rnsoft.colabademo

data class LoginResponse(
    val status: String?=null,
    val data: Data?=null,
    val message: String?=null,
    val code: String?=null
)

data class Data(
    val isLoggedIn: Boolean,
    val refreshToken: String,
    val refreshTokenValidTo: String,
    val token: String,
    val userName: String,
    val firstName: String,
    val lastName: String,
    val userProfileId: Int,
    val validFrom: String,
    val validTo: String,
    val tokenType: Int,
    val tokenTypeName: String
)