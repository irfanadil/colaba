package com.rnsoft.colabademo

import android.util.Log
import java.io.IOException
import javax.inject.Inject

class SignUpFlowDataSource @Inject constructor(private val serverApi: ServerApi){

    suspend fun sendOtpService(intermediateToken: String , phoneNumber:String): Result<OtpSentResponse> {
        return try {
            val otpResponse = serverApi.sendTwoFaToNumber(IntermediateToken = intermediateToken, PhoneNumber = phoneNumber)
            Log.e("otp-", otpResponse.toString())
            Result.Success(otpResponse)
        } catch (e: Throwable) {
            if(e is NoConnectivityException) {
                Log.e("network", "issues...")
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            }
            else
            Result.Error(IOException("Error logging in", e))
        }
    }


}