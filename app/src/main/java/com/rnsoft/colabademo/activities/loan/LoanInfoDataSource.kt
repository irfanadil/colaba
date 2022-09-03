package com.rnsoft.colabademo

import android.util.Log
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

/**
 * Created by Anita Kiran on 10/13/2021.
 */
class LoanInfoDataSource @Inject constructor(private val serverApi: ServerApi) {

    suspend fun getLoanInfoDetails(
        loanApplicationId: Int
    ): Result<LoanInfoDetailsModel> {
        return try {
            val response = serverApi.getLoanInfoDetails(loanApplicationId)
            //Log.e("Loan_info_Response", response.toString())
            Result.Success(response)
        } catch (e: Throwable) {
            if (e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error notification -", e))
        }
    }

    suspend fun getLoanGoals(loanPurposeId: Int): Result<ArrayList<LoanGoalModel>> {
        return try {
            val response = serverApi.getLoanGoals(loanPurposeId)
            Result.Success(response)
        } catch (e: Throwable) {
            if (e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
             else
                Result.Error(IOException("Error notification -", e))
        }
    }

    suspend fun addUpdateLoan(data:AddLoanInfoModel): Result<AddUpdateDataResponse> {
        val serverResponse: AddUpdateDataResponse
        return try {
            //val newToken = "Bearer $token"
            serverResponse = serverApi.addUpdateLoanInfo(data)
            if(serverResponse.status.equals("OK", true) )
                Result.Success(serverResponse)
            else
                Result.Success(serverResponse)

        } catch (e: Throwable){
            if(e is HttpException){
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            }
            else {
                Result.Error(IOException("Error logging in", e))
            }
        }
    }

    suspend fun addUpdateLoanRefinance(data:UpdateLoanRefinanceModel): Result<AddUpdateDataResponse> {
        val serverResponse: AddUpdateDataResponse
        return try {
            //val newToken = "Bearer $token"
            serverResponse = serverApi.addUpdateLoanRefinance(data)
                //Log.e("Add-Refinance-Response","$serverResponse")
            if(serverResponse.status.equals("OK", true) )
                Result.Success(serverResponse)
            else {
                Result.Success(serverResponse)
            }

        } catch (e: Throwable){
            //Log.e("errorrr",e.localizedMessage)
            if(e is HttpException){
               // Log.e("network", "issues...")
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            }
            else {
               // Log.e("erorr",e.message ?:"Error")
                Result.Error(IOException("Error logging in", e))
            }
        }
    }
}