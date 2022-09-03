package com.rnsoft.colabademo

import com.google.gson.annotations.SerializedName

data class LoginRequest (
    @SerializedName("Email") val Email: String  ,
    @SerializedName("Password") val Password: String
 //var Password: String
 )