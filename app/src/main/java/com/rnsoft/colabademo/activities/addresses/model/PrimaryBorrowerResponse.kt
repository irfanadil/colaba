package com.rnsoft.colabademo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class PrimaryBorrowerResponse(
    val code: String?,
    @SerializedName("data")
    val borrowerData: PrimaryBorrowerData?,
    val message: String?,
    val status: String?
)

data class PrimaryBorrowerData(
    val loanApplicationId: Int,
    val borrowerId: Int?,
    val borrowerBasicDetails: BorrowerBasicDetails?,
    val borrowerCitizenship: BorrowerCitizenship?,
    val currentAddress: CurrentAddress?,
    val maritalStatus: MaritalStatus?,
    val militaryServiceDetails: MilitaryServiceDetails? = null,
    val previousAddresses: List<PreviousAddresses>? = null
)

data class BorrowerBasicDetails(
    val borrowerId: Int?,
    val loanApplicationId: Int,
    val cellPhone: String?,
    val emailAddress: String?,
    val firstName: String?,
    val homePhone: String?,
    val lastName: String?,
    val middleName: String?,
    val ownTypeId: Int?,
    val suffix: String?,
    val workPhone: String?,
    val workPhoneExt: String?
)

@Parcelize
data class BorrowerCitizenship(
    val borrowerId: Int? = null,
    val loanApplicationId: Int? = null,
    val dependentAges: String? = null,
    val dependentCount: Int? = null,
    val dobUtc: String? = null,
    val residencyStatusExplanation: String? = null,
    val residencyStatusId: Int? = null,
    val residencyTypeId: Int? = null,
    val ssn: String? = null
) : Parcelable
















@Parcelize
data class CurrentAddress(
    val loanApplicationId: Int? = null,
    val borrowerId: Int? = null,
    val id: Int? = null,
    val housingStatusId: Int? = null,
    val addressModel: AddressModel? = null,
    val fromDate: String? = null,
    val isMailingAddressDifferent: Boolean? = null,
    val mailingAddressModel: AddressModel? = null,
    val monthlyRent: Double? = null
) : Parcelable

@Parcelize
data class PreviousAddresses(
    val position: Int? = null,
    val id: Int? = null,
    val housingStatusId: Int? = null,
    val monthlyRent: Double? = null,
    val fromDate: String? = null,
    val toDate: String? = null,
    val addressModel: AddressModel? = null,
    val isMailingAddressDifferent: Boolean? = null,
    val mailingAddressModel: AddressModel? = null
) :Parcelable

@Parcelize
data class AddressModel(
    val city: String? = null,
    val countryId: Int? = null,
    val countryName: String? = null,
    val countyId: Int? = null,
    val countyName: String? = null,
    val stateId: Int? = null,
    val stateName: String? = null,
    val street: String? = null,
    val unit: String? = null,
    val zipCode: String? = null
) : Parcelable


@Parcelize
data class MaritalStatus(
    val loanApplicationId: Int? = null,
    val borrowerId: Int? = null,
    val maritalStatusId: Int?=null,
    val firstName: String?=null,
    val middleName: String?=null,
    val lastName: String?= null,
    val relationWithPrimaryId: Int? = null,
    val isInRelationship: Boolean?=null,
    val otherRelationshipExplanation: String?=null,
    val relationFormedStateId: Int?=null,
    val relationshipTypeId: Int? = null,
    val spouseBorrowerId: Int? = null,
    val spouseLoanContactId: Int?= null,
    val spouseMaritalStatusId: Int? = null
) : Parcelable


data class MilitaryServiceDetails(
    val details: List<MilitaryServiceDetail>? = null,
    val isVaEligible: Boolean? = null
)

data class MilitaryServiceDetail(
    val expirationDateUtc: String? ,
    val militaryAffiliationId: Int?,
    val reserveEverActivated: Boolean?
)
