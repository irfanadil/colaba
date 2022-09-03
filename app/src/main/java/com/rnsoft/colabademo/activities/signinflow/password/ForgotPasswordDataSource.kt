package com.rnsoft.colabademo

import android.util.Log
import java.io.IOException
import javax.inject.Inject

class ForgotPasswordDataSource @Inject constructor(private val serverApi: ServerApi) {

    suspend fun forgotPasswordService(userEmail: String): Result<ForgotPasswordResponse> {
        return try {
           val forgotPasswordResponse = serverApi.forgotPasswordRequest(ForgotRequest(userEmail))
            Log.e("testAPI-forgot-service-", forgotPasswordResponse.toString())
            Result.Success(forgotPasswordResponse)
        //Result.Success(ForgotPasswordResponse("200",null,"some good", null))
        } catch (e: Throwable) {
            Result.Error(IOException("Error logging in", e))
        }
    }
}