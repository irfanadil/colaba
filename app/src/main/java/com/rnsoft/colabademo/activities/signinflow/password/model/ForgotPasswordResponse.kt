package com.rnsoft.colabademo

data class ForgotPasswordResponse(
    val code: String,
    val `data`: Any?,
    val message: String?,
    val status: String?
)