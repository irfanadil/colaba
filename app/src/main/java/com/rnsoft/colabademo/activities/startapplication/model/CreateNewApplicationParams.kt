package com.rnsoft.colabademo

data class CreateNewApplicationParams(
    var EmailAddress: String?=null,
    var FirstName: String?=null,
    var LastName: String?=null,
    var LoanGoal: Int?=null,
    var LoanOfficerUserId: Int?=null,
    var LoanPurpose: Int?=null,
    var MobileNumber: String?=null,
    var branchId: Int?=null,
    var contactId: Int?= null
)