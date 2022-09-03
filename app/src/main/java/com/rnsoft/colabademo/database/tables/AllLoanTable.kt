package com.rnsoft.colabademo.database.tables


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rnsoft.colabademo.Detail

@Entity(tableName = "allLoanTable")
data class AllLoanTable(
    var loanFilter:Int = 0,
    var activityTime: String?= null,
    var cellNumber: String?= null,
    var coBorrowerCount: Int?=null,
    var loanAmount: Int? = null,
    var propertyValue: Int?= null,
    var city: String?= null,
    var countryId: Int?= null,
    var countryName: String?= null,
    var stateId: Int?= null,
    var stateName: String?= null,
    var street: String?= null,
    var unit: String?= null,
    var zipCode: String?= null,
    var documents: Int?= null,
    var email: String?= null,
    var firstName: String?= null,
    var lastName: String?= null,
    var loanApplicationId: Int?= null,
    var loanPurpose: String?= null,
    var milestone: String?= null,
    var recycleCardState:Boolean? = false)

{
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}