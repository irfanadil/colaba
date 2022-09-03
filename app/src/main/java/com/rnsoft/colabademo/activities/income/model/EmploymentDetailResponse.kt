package com.rnsoft.colabademo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class EmploymentDetailResponse(
    val code: String?,
    @SerializedName("data")
    val employmentData: EmploymentData?,
    val message: String?,
    val status: String?
)

data class EmploymentData(
    val loanApplicationId: Int?,
    val borrowerId: Int?,
    val employerAddress: AddressData?,
    val employmentInfo: EmploymentInfo?,
    val employmentOtherIncome: List<EmploymentOtherIncome>?,
    val wayOfIncome: WayOfIncome?
)


@Parcelize
data class EmployerAddress(
    val borrowerId: Int? = null,
    val cityId: Int? = null,
    val cityName: String?= null,
    val countryId: Int?= null,
    val countryName: String? = null,
    val incomeInfoId: Int? = null,
    val loanApplicationId: Int? = null,
    val stateId: Int? = null,
    val stateName: String? = null,
    val streetAddress: String? = null,
    val unitNo: String? = null,
    val zipCode: String? = null
) : Parcelable


data class EmploymentInfo(
    val borrowerId: Int?,
    val employedByFamilyOrParty: Boolean?,
    val employerName: String?,
    val employerPhoneNumber: String?,
    //val employmentCategory: Any?, not sure what is the use of these two parameters
    //val isCurrentIncome: Boolean?,
    val endDate: String?,
    val hasOwnershipInterest: Boolean?,
    val incomeInfoId: Int?,
    val jobTitle: String?,
    val ownershipInterest: Double?,
    val startDate: String?,
    val yearsInProfession: Int?
)

data class EmploymentOtherIncome(
    val incomeTypeId: Int?,
    val name: String?,
    val displayName: String?,
    val annualIncome: Double?
)


data class WayOfIncome(
    val employerAnnualSalary: Double?,
    val hourlyRate: Double?,
    val hoursPerWeek: Int?,
    val isPaidByMonthlySalary: Boolean?
)