package com.rnsoft.colabademo

import com.rnsoft.colabademo.activities.model.StatesModel
import javax.inject.Inject

/**
 * Created by Anita Kiran on 10/15/2021.
 */
class RealEstateRepo @Inject constructor(private val dataSource: RealEstateDataSource){

    suspend fun getRealEstateDetails(token:String ,loanApplicationId:Int,borrowerPropertyId:Int): Result<RealEstateResponse> {
        return dataSource.getRealEstateDetails(
            token = token,
            loanApplicationId = loanApplicationId,
            borrowerPropertyId = borrowerPropertyId
        )
    }

    suspend fun getRealEstateSecondMortgageDetails(token:String ,loanApplicationId:Int,borrowerPropertyId:Int): Result<RealEstateSecondMortgageModel> {
        return dataSource.getRealEstateSecondMortgage(
            token = token,
            loanApplicationId = loanApplicationId,
            borrowerPropertyId = borrowerPropertyId
        )
    }

    suspend fun getRealEstateFirstMortgageDetails(token:String ,loanApplicationId:Int,borrowerPropertyId:Int): Result<RealEstateFirstMortgageModel> {
        return dataSource.getRealEstateFirstMortgage(
            token = token,
            loanApplicationId = loanApplicationId,
            borrowerPropertyId = borrowerPropertyId
        )
    }
    suspend fun getPropertyType(token:String): Result<ArrayList<DropDownResponse>> {
            return dataSource.getPropertyTypes(token = token)
    }

    suspend fun getOccupancyType(token:String): Result<ArrayList<DropDownResponse>> {
        return dataSource.getOccupancyType(token = token)
    }

    suspend fun getPropertyStatus(token:String): Result<ArrayList<DropDownResponse>> {
        return dataSource.getPropertyStatus(token = token)
    }

    suspend fun getCountries(token:String): Result<ArrayList<CountriesModel>> {
        return dataSource.getCountries(token = token)
    }

    suspend fun getCounties(token:String): Result<ArrayList<CountiesModel>> {
        return dataSource.getCounties(token = token)
    }

    suspend fun getStates(token:String): Result<ArrayList<StatesModel>> {
        return dataSource.getStates(token = token)
    }

    suspend fun sendRealEstateDetails(token: String, data: AddRealEstateResponse): Result<AddUpdateDataResponse> {
        return dataSource.sendRealEstateDetails(token,data)
    }

    suspend fun deleteRealEstate(token: String, borrowerPropertyId:Int): Result<AddUpdateDataResponse>{
        return dataSource.deleteRealEstate(token,borrowerPropertyId)
    }
}
