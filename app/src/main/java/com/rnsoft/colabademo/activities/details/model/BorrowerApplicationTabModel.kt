package com.rnsoft.colabademo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class BorrowerApplicationTabModel(
    val code: String,
    @SerializedName("data") val borrowerAppData : BorrowerAppData?,
    val message: String?,
    val status: String
)

data class BorrowerAppData(
    val assetAndIncome: AssetAndIncome?,
    val borrowerQuestionsModel: ArrayList<BorrowerQuestionsModel>?,
    val borrowersInformation: ArrayList<BorrowersInformation>?,
    val loanInformation: LoanInformation?,
    val realStateOwns: ArrayList<RealStateOwn>?,
    val subjectProperty: SubjectProperty?
)

data class AssetAndIncome(
    val totalAsset: Double,
    val totalMonthyIncome: Double
)

@Parcelize
data class BorrowersInformation(
    val borrowerId: Int,
    val firstName: String? = null,
    val genderId: Int?,
    val genderName: String?,
    val lastName: String? = null,
    val middleName:String?,
    val ownTypeName: String?,
    val owntypeId: Int,
    val ethnicities: ArrayList<Ethnicity>?,
    val races: ArrayList<Race>?,
    val isFooter:Boolean = false
) : Parcelable

@Parcelize
data class Ethnicity(
    val id: Int?,
    val name: String?,
    val ethnicityDetails: ArrayList<EthnicityDetail>?
) : Parcelable

@Parcelize
data class EthnicityDetail(
    val id: Int?,
    val name: String?
) : Parcelable


@Parcelize
data class Race(
    val id: Int?,
    val name: String?,
    val raceDetails: ArrayList<RaceDetail>
) : Parcelable

@Parcelize
data class RaceDetail(
    val id: Int?,
    val name: String?
) : Parcelable

data class LoanInformation(
    val downPayment: Double,
    val downPaymentPercent: Double,
    val loanAmount: Double,
    val loanPurposeDescription: String,
    val loanPurposeId: Int
)


data class BorrowerQuestionsModel(
    val questionDetail: QuestionDetail?,
    val questionResponses: ArrayList<QuestionResponse>?,
    val isDemoGraphic:Boolean = false,
    val races: ArrayList<Race>?,
    val ethnicities: ArrayList<Ethnicity>?,
    val eachBorrowerRaceEthnicity: ArrayList<BorrowersInformation>?= null
)

data class QuestionDetail(
    val questionHeader: String,
    val questionId: Int,
    val questionText: String

)

data class QuestionResponse(
    val borrowerFirstName: String,
    val borrowerId: Int,
    val borrowerLastName: String,
    val questionId: Int,
    val questionResponseText: String
)

data class RealStateOwn(
    @SerializedName("address") val realStateAddress : RealStateAddress?,
    val borrowerId: Int,
    val propertyInfoId: Int,
    val propertyTypeId: Int,
    val propertyTypeName: String,
    val isFooter:Boolean = false,
    val borrowerPropertyId: Int
)

data class RealStateAddress(
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
)

data class SubjectProperty(
    @SerializedName("address") val subjectPropertyAddress : SubjectPropertyAddress?,
    val propertyInfoId: Int,
    val propertyTypeId: Int,
    val propertyTypeName: String,
    val propertyUsageId: Int,
    val propertyUsageName: String,
    val propertyUsageDescription: String
)

data class SubjectPropertyAddress(
    val city: String?,
    val countryId: Int?,
    val countryName: String?,
    val countyId: String?,
    val countyName: String?,
    val stateId: Int?,
    val stateName: String?,
    val street: String?,
    val unit: String?,
    val zipCode: String?
)

