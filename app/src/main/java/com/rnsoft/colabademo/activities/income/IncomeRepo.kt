package com.rnsoft.colabademo

import javax.inject.Inject

/**
 * Created by Anita Kiran on 11/1/2021.
 */

class IncomeRepo @Inject constructor(private val dataSource: IncomeDataSource) {

    suspend fun getEmploymentDetails(token: String, loanApplicationId: Int, borrowerId: Int, incomeInfoId: Int): Result<EmploymentDetailResponse> {
        return dataSource.getEmploymentDetails(token = token, loanApplicationId, borrowerId, incomeInfoId)
    }

    suspend fun getSelfEmploymentData(token: String,borrowerId: Int, incomeInfoId: Int): Result<SelfEmploymentResponse> {
        return dataSource.getSelfEmploymentDetails(token = token,borrowerId, incomeInfoId)
    }

    suspend fun getBusinessIncome(token: String,borrowerId: Int, incomeInfoId: Int): Result<BusinessIncomeResponse> {
        return dataSource.getBusinessIncome(token = token,borrowerId, incomeInfoId)
    }

    suspend fun getMilitaryIncome(token: String,borrowerId: Int, incomeInfoId: Int): Result<MilitaryIncomeResponse> {
        return dataSource.getMilitaryIncome(token = token,borrowerId, incomeInfoId)
    }

    suspend fun getRetirementIncome(token: String,borrowerId: Int, incomeInfoId: Int): Result<RetirementIncomeResponse> {
        return dataSource.getRetirementIncome(token = token,borrowerId, incomeInfoId)
    }

    suspend fun getRetirementIncomeTypes(token: String): Result<ArrayList<DropDownResponse>> {
        return dataSource.getRetirementIncomeTypes(token = token)
    }

    suspend fun getOtherIncome(token: String, incomeInfoId: Int): Result<OtherIncomeResponse> {
        return dataSource.getOtherIncome(token = token, incomeInfoId)
    }

    suspend fun getOtherIncomeIncomeTypes(token: String): Result<ArrayList<DropDownResponse>> {
        return dataSource.getOtherIncomeTypes(token = token)
    }

    suspend fun getBusinessTypes(token: String): Result<ArrayList<DropDownResponse>> {
        return dataSource.getBusinessTypes(token = token)
    }

    suspend fun sendSelfEmploymentData(token: String, selfEmploymentData: SelfEmploymentData): Result<AddUpdateDataResponse> {
        val sendDataResponse = dataSource.sendSelfEmploymentContractorData(token,selfEmploymentData)
        return sendDataResponse
    }

    suspend fun sendCurrentEmploymentData(token: String, employmentData: AddCurrentEmploymentModel): Result<AddUpdateDataResponse> {
        return dataSource.sendCurrentEmploymentData(token,employmentData)
    }

    suspend fun sendBusinessData(token: String, businessData: BusinessData): Result<AddUpdateDataResponse> {
        return dataSource.sendBusinessIncomeData(token,businessData)
    }

    suspend fun sendRetirementData(token: String, retirementData: RetirementIncomeData): Result<AddUpdateDataResponse> {
        return dataSource.sendRetirementIncomeData(token,retirementData)
    }

    suspend fun sendOtherIncomeData(token: String, otherData: AddOtherIncomeInfo): Result<AddUpdateDataResponse> {
        return dataSource.sendOtherIncomeData(token,otherData)
    }

    suspend fun sendPrevEmploymentData(token: String, employmentData: PreviousEmploymentData): Result<AddUpdateDataResponse> {
        return dataSource.sendPrevEmploymentData(token,employmentData)
    }

    suspend fun sendMilitaryData(token: String, data:MilitaryIncomeData): Result<AddUpdateDataResponse> {
        return dataSource.sendMilitaryIncomeData(token,data)
    }

    suspend fun deleteIncomeSource(token : String,  incomeInfoId:Int, borrowerId:Int, loanApplicationId:Int): Result<GenericAddUpdateAssetResponse>{
        return dataSource.deleteIncome(token = token, incomeInfoId = incomeInfoId, borrowerId = borrowerId, loanApplicationId = loanApplicationId)
    }


}