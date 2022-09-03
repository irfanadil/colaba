package com.rnsoft.colabademo

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class SignUpFlowRepository @Inject
constructor(
    private val signUpFlowDataSource: SignUpFlowDataSource, private val spEditor: SharedPreferences.Editor,
    @ApplicationContext val applicationContext: Context
)
{

    suspend fun sendOtpToPhone(intermediateToken: String, phoneNumber:String): Result<OtpSentResponse> {
        spEditor.putString(AppConstant.phoneNumber, phoneNumber).apply()
        val otpResponseResult = signUpFlowDataSource.sendOtpService(intermediateToken,phoneNumber)
        if(otpResponseResult is Result.Success)
            setUpOtpInfo(otpResponseResult.data)
        return otpResponseResult

    }

    private fun setUpOtpInfo(otpSentResponse: OtpSentResponse){
        otpSentResponse.message?.let {
            spEditor.putString(AppConstant.otp_message, otpSentResponse.message)
                .apply()
        }
        otpSentResponse.otpData?.let {
            val gson = Gson()
            val otpData = gson.toJson(otpSentResponse.otpData)
            spEditor.putString(AppConstant.otpDataJson, otpData).apply()
            spEditor.putInt(AppConstant.secondsCount, 0).apply()
        }
    }



}