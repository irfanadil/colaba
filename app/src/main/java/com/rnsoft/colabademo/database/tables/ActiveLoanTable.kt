package com.rnsoft.colabademo.database.tables


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activeLoanTable")
data class ActiveLoanTable(val activityTime: String?,
                           val cellNumber: String?,
                           val coBorrowerCount: Int?,
                           val loanAmount: Int?,
                           val propertyValue: Int?,
                           val city: String?,
                           val countryId: Int?,
                           val countryName: String?,
                           val stateId: Int?,
                           val stateName: String?,
                           val street: String?,
                           val unit: String?,
                           val zipCode: String?,
                           val documents: Int?,
                           val email: String?,
                           val firstName: String?,
                           val lastName: String?,
                           val loanApplicationId: Int?,
                           val loanPurpose: String?,
                           val milestone: String?,
                           var recycleCardState:Boolean? = false) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}