package com.rnsoft.colabademo

import android.util.Log
import java.io.IOException
import javax.inject.Inject

class OtpDataSource @Inject constructor(private val serverApi: ServerApi)
{
    suspend fun verifyOtpService(intermediateToken: String , PhoneNumber:String, Otp:Int): Result<OtpVerificationResponse> {
        return try {
            val otpResponse = serverApi.verifyOtpCode(IntermediateToken = intermediateToken, OtpRequest(PhoneNumber, Otp))
            Log.e("otp-", otpResponse.toString())
            Result.Success(otpResponse)
        } catch (e: Throwable) {
            if(e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
            Result.Error(IOException("Error logging in", e))
        }
    }

    suspend fun notAskForOtpAgain(token: String ): Result<NotAskForOtpResponse> {
        return try {
            val newToken = "Bearer "+token
            val response = serverApi.notAskForOtpAgain(Authorization = newToken)
            Log.e("notAskFor-", response.toString())
            Result.Success(response)
        } catch (e: Throwable) {
            if(e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
            Result.Error(IOException("Error logging in", e))
        }
    }


}