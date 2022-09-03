package com.rnsoft.colabademo

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Created by Anita Kiran on 10/29/2021.
 */
class PrimaryBorrowerRepo @Inject constructor(private val dataSource : PrimaryBorrowerDataSource, @ApplicationContext val applicationContext: Context) {

    suspend fun getPrimaryBorrowerDetails(token: String, loanApplicationId : Int, borrowerId : Int): Result<PrimaryBorrowerResponse> {
        return dataSource.getPrimaryBorrowerDetails(token = token, loanApplicationId,borrowerId)
    }

    suspend fun getHousingStatus(token: String): Result<ArrayList<OptionsResponse>> {
        return dataSource.getHousingStatus(token = token)
    }

    suspend fun getRelationshipTypes(token: String): Result<ArrayList<DropDownResponse>> {
        return dataSource.getRelationshipTypes(token = token)
    }

    suspend fun getCitizenshipResponse(token: String): Result<ArrayList<OptionsResponse>> {
        return dataSource.getCitizenship(token = token)
    }

    suspend fun getMilitaryAffiliation(token: String): Result<ArrayList<OptionsResponse>> {
        return dataSource.getMilitaryAffiliation(token = token)
    }

    suspend fun getVisaStatus(token: String, residencyTypeId : Int): Result<ArrayList<OptionsResponse>> {
        return dataSource.getVisaStatus(token = token,residencyTypeId)
    }

    suspend fun sendBorrowerInfo(token: String, data: PrimaryBorrowerData): Result<AddUpdateDataResponse> {
        return dataSource.addUpdateBorrowerInfo(token,data)
    }

    suspend fun deletePreviousAddress(token: String, loanApplicationId: Int, id:Int): Result<AddUpdateDataResponse>{
        return dataSource.deletePreviousAddress(token,loanApplicationId,id)
    }

}