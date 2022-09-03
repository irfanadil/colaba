package com.rnsoft.colabademo

data class LoginResponseResult(
    val success: LoginResponse? = null,
    val screenNumber:Int? = 0,
    val emailError: String? = null,
    val passwordError: String? = null,
    val responseError:String? = null
)