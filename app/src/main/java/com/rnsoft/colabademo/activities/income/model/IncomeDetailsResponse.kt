package com.rnsoft.colabademo

import android.view.View
import com.google.gson.annotations.SerializedName

data class IncomeDetailsResponse(
    val code: String,
    @SerializedName("data") val incomeData: IncomeData?,
    val message: String?,
    val status: String?,
    var passedBorrowerId:Int?
)

data class IncomeData(
    val borrower : BorrowerIncomeData?
)

data class BorrowerIncomeData(
    val borrowerId: Int?,
    val borrowerName: String?,
    val ownTypeId: Int?,
    val ownTypeName: String?,
    val ownTypeDisplayName: String?,
    val monthlyIncome: Double?,
    val borrowerIncomes: ArrayList<BorrowerIncome>?
    )

data class BorrowerIncome(
    val incomeCategory: String?,
    val incomeCategoryTotal: Double?,
    val incomes: ArrayList<Income>?,
    var listenerAttached: View.OnClickListener= View.OnClickListener { }

)

data class Income(
    val employmentCategory: EmploymentCategory?,
    val incomeId: Int?,
    val incomeName: String?,
    val incomeTypeDisplayName: String?,
    val incomeTypeId: Int?,
    val incomeValue: Double?,
    val isCurrentIncome: Boolean?,
    val isDisabledByTenant: Boolean?,
    val jobTitle: String?,
    val startDate: String?,
    val endDate: String?
)



data class EmploymentCategory(
    val categoryDisplayName: String?,
    val categoryId: Int?,
    val categoryName: String?
)