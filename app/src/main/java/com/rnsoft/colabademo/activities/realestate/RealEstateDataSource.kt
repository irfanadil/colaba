package com.rnsoft.colabademo

import android.util.Log
import com.rnsoft.colabademo.activities.model.StatesModel
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

/**
 * Created by Anita Kiran on 10/15/2021.
 */
class RealEstateDataSource @Inject constructor(private val serverApi: ServerApi) {

    suspend fun getRealEstateDetails(
        token: String, loanApplicationId: Int, borrowerPropertyId: Int): Result<RealEstateResponse> {
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.getRealEstateDetails(newToken,loanApplicationId,borrowerPropertyId)
            Log.e("RealEstate-Reponse", response.toString())
            Result.Success(response)
        } catch (e: Throwable) {
            Timber.e(e.message + e.cause)
            if (e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else {
                Result.Error(IOException("Error notification -", e))
            }
        }
    }

    suspend fun getRealEstateSecondMortgage(
        token: String,
        loanApplicationId: Int,
        borrowerPropertyId: Int
    ): Result<RealEstateSecondMortgageModel> {
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.getSecMortgageDetails(newToken,loanApplicationId,borrowerPropertyId)
            //Log.e("RealEstate-Reponse", response.toString())
            Result.Success(response)
        } catch (e: Throwable) {
            Timber.e(e.message + e.cause)
            if (e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else {
                Result.Error(IOException("Error notification -", e))
            }
        }
    }

    suspend fun getRealEstateFirstMortgage(
        token: String,
        loanApplicationId: Int,
        borrowerPropertyId: Int
    ): Result<RealEstateFirstMortgageModel> {
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.getFirstMortgageDetails(newToken,loanApplicationId,borrowerPropertyId)
            Log.e("FirstMortgage-Reponse", response.toString())
            Result.Success(response)
        } catch (e: Throwable) {
            Timber.e(e.message + e.cause)
            if (e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else {
                Result.Error(IOException("Error notification -", e))
            }
        }
    }

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

    suspend fun getPropertyStatus(token: String): Result<ArrayList<DropDownResponse>> {
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.getPropertyStatus(newToken)
            Result.Success(response)
        } catch (e: Throwable) {
            if (e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error notification -", e.cause))
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
            Result.Success(response)
        } catch (e: Throwable) {
            if (e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error notification -", e.cause))
        }
    }

    suspend fun sendRealEstateDetails(token: String, data: AddRealEstateResponse): Result<AddUpdateDataResponse> {
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.addRealEstateDetails(newToken,data)
            //Log.e("Send--realEstate-respone","$response")
            Result.Success(response)
        } catch (e: Throwable){
            if(e is HttpException){
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            }
            else {
               // Log.e("add-business-Error",e.localizedMessage)
                Result.Error(IOException("Error logging in", e))
            }
        }
    }

    suspend fun deleteRealEstate(token: String,borrowerPropertyId: Int): Result<AddUpdateDataResponse> {
        //Log.e("-dataSource-Delete-borrowerPropertyId" , ""+ borrowerPropertyId)
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.deleteRealEstate(newToken, borrowerPropertyId)
            //Timber.e("deleteRealEstate = $response")
            Result.Success(response)
        } catch (e: Throwable) {
            if (e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error notification -", e))
        }
    }

}