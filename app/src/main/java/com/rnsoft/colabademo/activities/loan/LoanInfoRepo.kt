package com.rnsoft.colabademo

import javax.inject.Inject

/**
 * Created by Anita Kiran on 10/13/2021.
 */
class LoanInfoRepo @Inject constructor(private val datasource : LoanInfoDataSource) {

    suspend fun getLoanInfo(loanApplicationId: Int): Result<LoanInfoDetailsModel> {
        return datasource.getLoanInfoDetails(loanApplicationId = loanApplicationId)
    }

    suspend fun getLoanGoals(loanPurposeId: Int): Result<ArrayList<LoanGoalModel>> {
        return datasource.getLoanGoals(loanPurposeId = loanPurposeId)
    }

    suspend fun addLoanInfo(data: AddLoanInfoModel): Result<AddUpdateDataResponse> {
        val sendDataResponse = datasource.addUpdateLoan(data)
        return sendDataResponse
    }

    suspend fun addLoanRefinanceInfo(data: UpdateLoanRefinanceModel): Result<AddUpdateDataResponse> {
        val sendDataResponse = datasource.addUpdateLoanRefinance(data)
        return sendDataResponse
    }
}