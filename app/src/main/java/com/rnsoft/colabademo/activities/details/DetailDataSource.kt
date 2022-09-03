package com.rnsoft.colabademo

import okhttp3.ResponseBody
import retrofit2.Response
import timber.log.Timber
import java.io.File
import java.io.IOException
import javax.inject.Inject

class DetailDataSource  @Inject constructor(private val serverApi: ServerApi) {

    suspend fun getLoanInfo(token: String, loanApplicationId: Int): Result<BorrowerOverviewModel> {
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.getLoanInfo( loanApplicationId)
            //Log.e("getLoanInfo-", response.toString())
            Result.Success(response)
        } catch (e: Throwable) {
            if (e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error notification -", e))
        }
    }

    suspend fun getBorrowerDocuments(
        token: String,
        loanApplicationId: Int
    ): Result<ArrayList<BorrowerDocsModel>> {
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.getBorrowerDocuments( loanApplicationId)
            //Log.e("BorrowerDocsModel-", response.toString())
            Result.Success(response)
        } catch (e: Throwable) {
            if (e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error notification -", e))
        }
    }

    suspend fun getBorrowerApplicationTabData(
        token: String,
        loanApplicationId: Int
    ): Result<BorrowerApplicationTabModel> {
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.getBorrowerApplicationTabData(loanApplicationId)
            Timber.e(response.toString())
            Result.Success(response)
        } catch (e: Throwable) {
            if (e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error notification -", e))
        }
    }

    suspend fun downloadFile(token:String, id:String, requestId:String, docId:String, fileId:String):Response<ResponseBody>?{
        try {
            val newToken = "Bearer $token"
            val result = serverApi.downloadFile(
                id = id,
                requestId = requestId,
                docId = docId,
                fileId = fileId
            )
            Timber.e(result.body().toString())
            Timber.e(result.raw().toString())
            Timber.e(result.code().toString())
            Timber.e(result.errorBody().toString())
            Timber.e(result.errorBody()?.charStream().toString())
            Timber.e(result.errorBody()?.source().toString())


            return result
        } catch (e: Throwable) {
            /*
            if(e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error notification -", e))
             */

            return  null

        }
    }

    suspend fun getMilestoneForLoanCenter( loanApplicationId: Int): Result<AppMileStoneResponse> {
        return try {

            val response = serverApi.getMilestoneForLoanCenter( loanApplicationId)
            //Log.e("getLoanInfo-", response.toString())
            Result.Success(response)
        } catch (e: Throwable) {
            if (e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error notification -", e))
        }
    }



}