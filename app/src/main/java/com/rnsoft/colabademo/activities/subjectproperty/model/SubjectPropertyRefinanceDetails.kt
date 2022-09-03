package com.rnsoft.colabademo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Anita Kiran on 10/13/2021.
 */
data class SubjectPropertyRefinanceDetails(
    val code: String,
    @SerializedName("data") val subPropertyData : SubPropertyRefinanceData?,
    val message: String?,
    val status: String
)

@Parcelize
data class RefinanceAddressData(
    var street : String? = null,
    var unit : String? = null,
    var city : String? = null,
    var stateId : Int? = null,
    var zipCode : String? = null,
    var countryId : Int? = null,
    var countryName : String? = null,
    var stateName : String? = null,
    var countyId: Int? = null,
    var countyName : String? = null
) : Parcelable


data class SubPropertyRefinanceData(
    var loanApplicationId : Int,
    //var propertyInfoId : Int?,
    var propertyTypeId : Int?,
    var propertyUsageId : Int?,
    var rentalIncome : Double?,
    var isMixedUseProperty : Boolean?,
    var mixedUsePropertyExplanation : String?,
    var subjectPropertyTbd : Boolean?,
    var propertyValue: Double?,
    var hoaDues : Double?,
    var dateAcquired : String?,
    var isSameAsPropertyAddress: Boolean?,
    //var loanGoalId : Int?,
    //var loanAmount: Double?,
    //var cashOutAmount: Double?,
    var hasFirstMortgage : Boolean?,
    var hasSecondMortgage: Boolean?,
    var propertyTax: Double?,
    var floodInsurance : Double?,
    var homeOwnerInsurance: Double?,
    var firstMortgageModel : FirstMortgageModel?,
    var secondMortgageModel : SecondMortgageModel?,
    @SerializedName("address") val addressRefinance: AddressData?
)

@Parcelize
data class FirstMortgageModel(
    var propertyTaxesIncludeinPayment : Boolean? = null,
    var homeOwnerInsuranceIncludeinPayment : Boolean? = null ,
    var floodInsuranceIncludeinPayment : Boolean?= null ,
    var paidAtClosing : Boolean? = null,
    var firstMortgagePayment: Double? = null,
    var unpaidFirstMortgagePayment : Double?  = null,
    var helocCreditLimit : Double? = null,
    var isHeloc : Boolean? = null
) : Parcelable

@Parcelize
data class SecondMortgageModel(
    var secondMortgagePayment: Double? = null,
    var unpaidSecondMortgagePayment : Double? = null,
    var helocCreditLimit : Double?=null,
    var isHeloc: Boolean?=null,
    var combineWithNewFirstMortgage : Boolean? = null,
    var wasSmTaken: Boolean? = null,
    var paidAtClosing : Boolean? = null
) : Parcelable
