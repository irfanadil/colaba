package com.rnsoft.colabademo

import android.content.Context
import android.content.SharedPreferences
import com.rnsoft.colabademo.activities.model.StatesModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class BorrowerApplicationRepo  @Inject constructor(
    private val borrowerApplicationDataSource: BorrowerApplicationDataSource, private val spEditor: SharedPreferences.Editor,
    @ApplicationContext val applicationContext: Context
) {

   suspend fun getBorrowerAssetsDetail(token:String, loanApplicationId:Int, borrowerId:Int):Result<MyAssetBorrowerDataClass>{
        return borrowerApplicationDataSource.getBorrowerAssetsDetail(token = token , loanApplicationId = loanApplicationId, borrowerId = borrowerId )
    }


    suspend fun getGovernmentQuestionsList(token:String, loanApplicationId:Int, ownTypeId:Int, borrowerId:Int):Result<GovernmentQuestionsModelClass>{
        return borrowerApplicationDataSource.getGovernmentQuestionsList(
            token = token , loanApplicationId = loanApplicationId,
            ownTypeId = ownTypeId,
            borrowerId = borrowerId )
    }

    suspend fun getGovernmentQuestions(token:String, loanApplicationId:Int, ownTypeId:Int, borrowerId:Int):Result<GovernmentQuestionsModelClass>{
        return borrowerApplicationDataSource.getGovernmentQuestions(
            token = token , loanApplicationId = loanApplicationId,
            ownTypeId = ownTypeId,
            borrowerId = borrowerId )
    }

    suspend fun addOrUpdateGovernmentQuestions(token:String, questionParams:GovernmentParams ):Result<GovernmentAddUpdateDataResponse> {
        return borrowerApplicationDataSource.addOrUpdateGovernmentQuestions(
            token = token , questionParams = questionParams )
    }

    suspend fun addOrUpdateDemoGraphic(token:String, demoGraphicData:DemoGraphicData ):Result<AddUpdateDemoGraphicResponse> {
        return borrowerApplicationDataSource.addOrUpdateDemoGraphic(token = token , demoGraphicData = demoGraphicData )
    }


    suspend fun getBorrowerIncomeDetail(token:String, loanApplicationId:Int, borrowerId:Int):Result<IncomeDetailsResponse>{
        return borrowerApplicationDataSource.getBorrowerIncomeDetail(token = token , loanApplicationId = loanApplicationId, borrowerId = borrowerId )
    }






    suspend fun getDemoGraphicInfo(token:String, loanApplicationId:Int, borrowerId:Int):Result<DemoGraphicResponseModel>{
        return borrowerApplicationDataSource.getDemoGraphicInfo(
            token = token , loanApplicationId = loanApplicationId,
            borrowerId = borrowerId )
    }




    suspend fun getEthnicityList(token:String): Result<ArrayList<EthnicityResponseModel>> {
        return borrowerApplicationDataSource.getEthnicityList(token = token)
    }

    suspend fun getRaceList(token:String): Result<ArrayList<RaceResponseModel>> {
        return borrowerApplicationDataSource.getRaceList(token = token)
    }

    suspend fun getGenderList(token:String): Result<ArrayList<GenderResponseModel>> {
        return borrowerApplicationDataSource.getGenderList(token = token)
    }

    suspend fun getPropertyType(token:String): Result<ArrayList<DropDownResponse>> {
        return borrowerApplicationDataSource.getPropertyTypes(token = token)
    }

    suspend fun getOccupancyType(token:String): Result<ArrayList<DropDownResponse>> {
        return borrowerApplicationDataSource.getOccupancyType(token = token)
    }

    suspend fun getCountries(token:String): Result<ArrayList<CountriesModel>> {
        return borrowerApplicationDataSource.getCountries(token = token)
    }

    suspend fun getCounties(token:String): Result<ArrayList<CountiesModel>> {
        return borrowerApplicationDataSource.getCounties(token = token)
    }

    suspend fun getStates(token:String): Result<ArrayList<StatesModel>> {
        return borrowerApplicationDataSource.getStates(token = token)
    }

    suspend fun getSubjectPropertyDetails(token:String, loanApplicationId:Int):Result<SubjectPropertyDetails>{
        return borrowerApplicationDataSource.getSubjectPropertyDetails(token = token , loanApplicationId = loanApplicationId)
    }

    suspend fun getSubjectPropertyRefinance(token:String, loanApplicationId:Int):Result<SubjectPropertyRefinanceDetails>{
        return borrowerApplicationDataSource.getSubjectPropertyRefinance(token = token , loanApplicationId = loanApplicationId)
    }

}