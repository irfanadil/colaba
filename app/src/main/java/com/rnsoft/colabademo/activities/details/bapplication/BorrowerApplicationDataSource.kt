package com.rnsoft.colabademo

import android.util.Log
import com.rnsoft.colabademo.activities.model.StatesModel
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class BorrowerApplicationDataSource  @Inject constructor(private val serverApi: ServerApi) {

    suspend fun getPropertyTypes(token: String): Result<ArrayList<DropDownResponse>> {
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.getPropertyTypes(newToken)
            Result.Success(response)
        } catch (e: Throwable) {
            if (e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error notification -", e.cause))
        }
    }

    suspend fun getOccupancyType(token: String): Result<ArrayList<DropDownResponse>> {
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.getOccupancyType(newToken)
            Result.Success(response)
        } catch (e: Throwable) {
            if (e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error notification -", e.cause))
        }
    }


    suspend fun getSubjectPropertyDetails(
        token: String,
        loanApplicationId: Int
    ): Result<SubjectPropertyDetails> {
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.getSubjectPropertyDetails(newToken, loanApplicationId)
            Log.e("Sub-property-Details-------", response.toString())
            Result.Success(response)
        } catch (e: Throwable) {
            if (e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error notification -", e))
        }
    }

    suspend fun getSubjectPropertyRefinance(
        token: String,
        loanApplicationId: Int
    ): Result<SubjectPropertyRefinanceDetails> {
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.getSubjectPropertyRefinance(newToken, loanApplicationId)
            Log.e("Sub-property-Refinance", response.toString())
            Result.Success(response)
        } catch (e: Throwable) {
            if (e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error notification -", e))
        }
    }


    suspend fun getCountries(token: String): Result<ArrayList<CountriesModel>> {
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.getCountries(newToken)
            Result.Success(response)
        } catch (e: Throwable) {
            if (e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error notification -", e.cause))
        }
    }


    suspend fun getCounties(token: String): Result<ArrayList<CountiesModel>> {
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.getCounties(newToken)
            //Timber.e(" Response " + response.toString())
            Result.Success(response)

        } catch (e: Throwable) {
            if (e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error notification -", e.cause))
        }
    }

    suspend fun getStates(token: String): Result<ArrayList<StatesModel>> {
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.getStates(newToken)
            Timber.e(" GetStates " + response.toString())
            Result.Success(response)
        } catch (e: Throwable) {
            if (e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error notification -", e.cause))

        }
    }


    suspend fun getBorrowerAssetsDetail(
        token: String,
        loanApplicationId: Int,
        borrowerId:Int
    ): Result<MyAssetBorrowerDataClass> {
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.getBorrowerAssetsDetail( loanApplicationId = loanApplicationId , borrowerId = borrowerId)
            //Timber.e("AssetsModelDataClass-", response.toString())
            Result.Success(response)
        } catch (e: Throwable) {
            if (e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error notification -", e))
        }
    }


    suspend fun getGovernmentQuestionsList(
        token: String,
        loanApplicationId: Int,
        ownTypeId:Int, borrowerId:Int
    ): Result<GovernmentQuestionsModelClass> {
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.getGovernmentQuestions( loanApplicationId = loanApplicationId , ownTypeId = ownTypeId,  borrowerId = borrowerId)
            //Timber.e("AssetsModelDataClass-", response.toString())
            Result.Success(response)
        } catch (e: Throwable) {
            if (e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error notification -", e))
        }
    }

    suspend fun getBorrowerIncomeDetail(
        token: String,
        loanApplicationId: Int,
        borrowerId:Int
    ): Result<IncomeDetailsResponse> {
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.getBorrowerIncomeDetail(newToken, loanApplicationId = loanApplicationId , borrowerId = borrowerId)
            Timber.e("IncomeResponse-", response.toString())
            Result.Success(response)
        } catch (e: Throwable) {
            if (e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error notification -", e))
        }
    }


    suspend fun addOrUpdateGovernmentQuestions(token:String, questionParams:GovernmentParams):Result<GovernmentAddUpdateDataResponse>{
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.addOrUpdateGovernmentQuestions( newToken, questionParams )
            Timber.e("updateGovernmentQuestions - $response")
            Result.Success(response)
        } catch (e: Throwable) {
            if (e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error notification -", e))
        }
    }


    suspend fun addOrUpdateDemoGraphic( token:String, demoGraphicData:DemoGraphicData):Result<AddUpdateDemoGraphicResponse>{
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.addOrUpdateDemoGraphic( demoGraphicData )
            Timber.e("addOrUpdateDemoGraphic - $response")
            Result.Success(response)
        } catch (e: Throwable) {
            if (e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error notification -", e))
        }
    }


    suspend fun getGovernmentQuestions(
        token: String,
        loanApplicationId: Int,
        ownTypeId:Int, borrowerId:Int
    ): Result<GovernmentQuestionsModelClass> {
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.getGovernmentQuestions(  loanApplicationId = loanApplicationId , ownTypeId = ownTypeId,  borrowerId = borrowerId)
            Timber.e("GovernmentQuestionsModelClass - $response")
            Result.Success(response)
        } catch (e: Throwable) {
            if (e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error notification -", e))
        }
    }

    suspend fun getDemoGraphicInfo(
        token: String,
        loanApplicationId: Int,
        borrowerId:Int
    ): Result<DemoGraphicResponseModel> {
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.getDemoGraphicInfo(  loanApplicationId = loanApplicationId, borrowerId = borrowerId)
            Timber.e("DemoGraphicResponse - $response")
            Result.Success(response)
        } catch (e: Throwable) {
            if (e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error notification -", e))
        }
    }

    suspend fun getGenderList(token: String): Result<ArrayList<GenderResponseModel>> {
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.getGenderList()
            //Timber.e("GenderResponse - $response")
            Result.Success(response)
        } catch (e: Throwable) {
            if (e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error notification -", e.cause))
        }
    }

    suspend fun getEthnicityList(token: String): Result<ArrayList<EthnicityResponseModel>> {
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.getEthnicityList()
            //Timber.e("EthnicityResponse - $response")
            Result.Success(response)
        } catch (e: Throwable) {
            if (e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error notification -", e.cause))
        }
    }

    suspend fun getRaceList(token: String): Result<ArrayList<RaceResponseModel>> {
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.getRaceList()
            Timber.e("RaceResponse - $response")
            Result.Success(response)
        } catch (e: Throwable) {
            if (e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error notification -", e.cause))
        }
    }



}