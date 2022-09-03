package com.rnsoft.colabademo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Anita Kiran on 10/15/2021.
 */
data class RealEstateResponse(
    val code: String,
    @SerializedName("data") val data : RealEstateData?,
    val message: String?,
    val status: String
)
data class RealEstateData(
    val loanApplicationId:Int?,
    val address: AddressData?,
    val floodInsurance: Double?,
    val homeOwnerInsurance: Double?,
    val propertyTax: Double?,
    val firstMortgageBalance: Double?,
    val firstMortgagePayment: Double?,
    val hasFirstMortgage: Boolean?,
    val hasSecondMortgage: Boolean?,
    val hoaDues: Double?,
    val appraisedPropertyValue : Double?,
    val occupancyTypeId: Int?,
    val propertyInfoId: Int?,
    val propertyStatus: Int?,
    val propertyTypeId: Int?,
    val propertyValue: Double?,
    val rentalIncome: Double?,
    //val secondMortgageBalance: Double?,
    //val secondMortgagePayment: Double?
    var firstMortgageModel : FirstMortgageModel?,
    var secondMortgageModel : SecondMortgageModel?,
)

@Parcelize
data class RealEstateAddress(
    val city: String?,
    val countryId: Int?,
    val countryName: String?,
    val countyId: Int?,
    val countyName: String?,
    val stateId: Int?,
    val stateName: String?,
    val street: String?,
    val unit: String?,
    val zipCode: String?
) : Parcelable
