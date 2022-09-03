package com.rnsoft.colabademo

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class OtpRepo @Inject
constructor(
    private val otpDataSource : OtpDataSource, private val sharedPref: SharedPreferences.Editor,
    @ApplicationContext val applicationContext: Context
) {

    suspend fun startOtpVerification(intermediateToken: String, phoneNumber:String, otp:Int): Result<OtpVerificationResponse> {

        val otpResponseResult =  otpDataSource.verifyOtpService(intermediateToken, phoneNumber, otp)
        if(otpResponseResult is Result.Success)
            otpResponseResult.data.data?.let { storeLoggedInUserInfo(it) } // mark user as logged in completely...
        return otpResponseResult
    }

    private fun storeLoggedInUserInfo(data: Data) {

        if(AppSetting.biometricEnabled) {
            sharedPref.putBoolean(AppConstant.isbiometricEnabled, true).apply()
            AppSetting.biometricEnabled = false
        }

        sharedPref.putString(AppConstant.token, data.token).apply()

        sharedPref.putString(AppConstant.refreshToken, data.refreshToken)
            .apply()
        sharedPref.putInt(AppConstant.userProfileId, data.userProfileId)
            .apply()
        sharedPref.putString(AppConstant.userName, data.userName).apply()
        sharedPref.putString(AppConstant.firstName, data.firstName).apply()
        sharedPref.putString(AppConstant.lastName, data.lastName).apply()
        sharedPref.putString(AppConstant.validFrom, data.validFrom).apply()
        sharedPref.putString(AppConstant.validTo, data.validTo).apply()
        sharedPref.putInt(AppConstant.tokenType, data.tokenType).apply()
        sharedPref.putString(AppConstant.tokenTypeName, data.tokenTypeName)
            .apply()
        sharedPref.putString(
            AppConstant.refreshTokenValidTo,
            data.refreshTokenValidTo
        ).apply()

        if(data.tokenTypeName == AppConstant.AccessToken)
            sharedPref.putBoolean(AppConstant.IS_LOGGED_IN, true).apply() // mark user as logged in completely...
    }



    suspend fun notAskForOtpAgain(token: String): Result<NotAskForOtpResponse> {
        return otpDataSource.notAskForOtpAgain(token)
    }


}