package com.rnsoft.colabademo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BankruptcyAnswerData(
    var `1`: Boolean = false,
    var `2`: Boolean = false,
    var `3`: Boolean = false,
    var `4`: Boolean = false,
    var extraDetail:String = ""

):Parcelable



data class BankruptcyAnswerDataParam(
    var `1`: String =  "Chapter 7" ,
    var `2`: String = "Chapter 11",
    var `3`: String = "Chapter 12",
    var `4`: String = "Chapter 13"
)

/*
data class BankruptcyAnswerData(
    val liabilityName: String,
    val liabilityTypeId: String,
    val monthlyPayment: String,
    val name: String,
    val remainingMonth: String
)
 */