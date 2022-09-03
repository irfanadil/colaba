package com.rnsoft.colabademo.activities.details.boverview.model

data class TestOverviewModel(
    val address: Address,
    val borrowers: List<Borrower>,
    val cellPhone: String,
    val downPayment: Double,
    val email: String,
    val loanAmount: Double,
    val loanGoal: String,
    val loanNumber: Any,
    val loanPurpose: String,
    val milestone: String,
    val milestoneId: Int,
    val postedOn: Any,
    val propertyType: String,
    val propertyUsage: String,
    val propertyValue: Double
)

data class Borrower(
    val firstName: String,
    val lastName: String,
    val middleName: Any,
    val ownType: String,
    val ownTypeId: Int
)


data class Address(
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