package com.rnsoft.colabademo

import com.rnsoft.colabademo.FirstMortgageModel
import com.rnsoft.colabademo.RealEstateAddress
import com.rnsoft.colabademo.SecondMortgageModel

data class AddRealEstateResponse(
    val loanApplicationId:Int?,
    val propertyInfoId: Int?,
    var borrowerPropertyId : Int?,
    var borrowerId: Int?,
    val propertyTypeId: Int?,
    val occupancyTypeId: Int?,
    val propertyStatus: Int?,
    val hoaDues: Double?,
    val appraisedPropertyValue: Double?,
    val rentalIncome: Double?,
    val floodInsurance: Double?,
    val homeOwnerInsurance: Double?,
    val propertyTax: Double?,
    val hasFirstMortgage: Boolean?,
    val hasSecondMortgage: Boolean?,
    val address: AddressData?,
    var firstMortgageModel : FirstMortgageModel?,
    var secondMortgageModel : SecondMortgageModel?,
)

