package com.rnsoft.colabademo.activities.details.bapplication.test

data class TestAppModel(
    val code: String,
    val `data`: Data,
    val message: Any,
    val status: String
)

data class Data(
    val assetAndIncome: AssetAndIncome,
    val borrowerQuestionsModel: List<BorrowerQuestionsModel>,
    val borrowersInformation: List<BorrowersInformation>,
    val loanInformation: LoanInformation,
    val realStateOwns: List<RealStateOwn>,
    val subjectProperty: SubjectProperty
)


data class AssetAndIncome(
    val totalAsset: Double,
    val totalMonthyIncome: Double
)

data class BorrowerQuestionsModel(
    val questionDetail: QuestionDetail,
    val questionResponses: List<QuestionResponse>
)

data class BorrowersInformation(
    val borrowerId: Int,
    val ethnicities: List<Ethnicity>,
    val firstName: String,
    val genderId: Int,
    val genderName: String,
    val lastName: String,
    val ownTypeName: String,
    val owntypeId: Int,
    val races: List<Race>
)


data class RealStateOwn(
    val address: Address,
    val borrowerId: Int,
    val borrowerPropertyId: Int,
    val propertyInfoId: Int,
    val propertyTypeId: Int,
    val propertyTypeName: String
)

data class RaceDetail(
    val id: Int,
    val name: String
)




data class SubjectProperty(
    val address: AddressX,
    val propertyInfoId: Int,
    val propertyTypeId: Int,
    val propertyTypeName: String,
    val propertyUsageDescription: String,
    val propertyUsageId: Int,
    val propertyUsageName: String
)

data class Race(
    val id: Int,
    val name: String,
    val raceDetails: List<RaceDetail>
)

data class QuestionResponse(
    val borrowerFirstName: String,
    val borrowerId: Int,
    val borrowerLastName: String,
    val questionId: Int,
    val questionResponseText: String
)

data class QuestionDetail(
    val questionHeader: String,
    val questionId: Int,
    val questionText: String
)

data class LoanInformation(
    val downPayment: Double,
    val downPaymentPercent: Double,
    val loanAmount: Double,
    val loanPurposeDescription: String,
    val loanPurposeId: Int
)

data class EthnicityDetail(
    val id: Int,
    val name: String
)

data class Ethnicity(
    val ethnicityDetails: List<EthnicityDetail>,
    val id: Int,
    val name: String
)


data class AddressX(
    val city: String,
    val countryId: Int,
    val countryName: String,
    val countyId: Int,
    val countyName: String,
    val stateId: Int,
    val stateName: String,
    val street: String,
    val unit: String,
    val zipCode: String
)

data class Address(
    val city: String,
    val countryId: Int,
    val countryName: String,
    val countyId: Any,
    val countyName: Any,
    val stateId: Int,
    val stateName: String,
    val street: String,
    val unit: String,
    val zipCode: String
)