package com.rnsoft.colabademo

import android.util.Log
import java.io.IOException
import javax.inject.Inject

class PhoneNumberDataSource @Inject constructor(private val serverApi: ServerApi) {
    suspend fun skipTwoFactorService(intermediateToken: String): Result<SkipTwoFactorResponse> {
        return try {
            val skipTwoFactorResponse = serverApi.skipTwoFactorApi(intermediateToken)
            Result.Success(skipTwoFactorResponse)
        } catch (e: Throwable) {
            if(e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
            Result.Error(IOException("Error logging in", e))
        }
    }



}