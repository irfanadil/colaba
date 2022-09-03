package com.rnsoft.colabademo

import com.rnsoft.colabademo.activities.model.StatesModel
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

/**
 * Created by Anita Kiran on 10/28/2021.
 */
class CommonDataSource @Inject constructor(private val serverApi: ServerApi) {

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


    suspend fun getCoBorrowerOccupancyStatus(
        token: String,
        loanApplicationId: Int
    ): Result<CoBorrowerOccupancyStatus> {
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.getCoBorrowerOccupancyStatus(newToken, loanApplicationId)
            //Log.e("Co-Borrow-Occupnacy", response.toString())
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
            //Timber.e(" GetStates " + response.toString())
            Result.Success(response)
        } catch (e: Throwable) {
            if (e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error notification -", e.cause))

        }
    }

}