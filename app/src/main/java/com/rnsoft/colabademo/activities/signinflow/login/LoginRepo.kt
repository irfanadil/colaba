package com.rnsoft.colabademo

import android.content.SharedPreferences
import com.google.gson.Gson
import javax.inject.Inject

class LoginRepo @Inject
constructor(
    private val dataSource: LoginDataSource, private val spEditor: SharedPreferences.Editor
) {

   suspend fun validateLoginCredentials(
        userEmail: String,
        password: String,
        dontAskTwoFaIdentifier:String=""

    ): Result<LoginResponse> {
        spEditor.putBoolean(AppConstant.isbiometricEnabled, false).apply()
        val genericResult = dataSource.login(userEmail, password , dontAskTwoFaIdentifier )
        if (genericResult is Result.Success) {
            genericResult.data.data?.let { storeLoggedInUserInfo(it) }

        }
        return genericResult
    }

    suspend fun fetchTenantConfiguration(authString: String): Result<TenantConfigurationResponse> {
        val genericResult = dataSource.tenantConfigurationSource(authString)
        if (genericResult is Result.Success)
            storeTenantInfo(genericResult.data)
        return genericResult
    }

    suspend fun getPhoneNumberDetail(authString: String): Result<SendTwoFaResponse> {
        val genericResult = dataSource.getPhoneDetail(authString)
        if (genericResult is Result.Success)
            storePhoneInfo(genericResult.data)
        return genericResult
    }

    suspend fun otpSettingFromService(intermediateToken:String): Result<OtpSettingResponse>{
        spEditor.putInt(AppConstant.maxOtpSendAllowed, 5).apply() // Setting default value.........
        val result = dataSource.getOtpSetting(intermediateToken)
        if (result is Result.Success)
            storeOtpSetting(result.data)
        return result
    }

    private fun storeOtpSetting(otpSettingResponse: OtpSettingResponse){
        otpSettingResponse.otpSettingData?.let { settingData ->
            settingData.maxTwoFaSendAllowed?.let {
                spEditor.putInt(AppConstant.maxOtpSendAllowed, it).apply()
            }

            settingData.twoFaResendCoolTimeInMinutes?.let {
                spEditor.putInt(AppConstant.twoFaResendCoolTimeInMinutes, it).apply()
            }
        }
    }


    private fun storeLoggedInUserInfo(data: Data) {

        spEditor.putString(AppConstant.token, data.token).apply()

        spEditor.putString(AppConstant.refreshToken, data.refreshToken)
            .apply()
        spEditor.putInt(AppConstant.userProfileId, data.userProfileId)
            .apply()
        spEditor.putString(AppConstant.userName, data.userName).apply()
        spEditor.putString(AppConstant.firstName, data.firstName).apply()
        spEditor.putString(AppConstant.lastName, data.lastName).apply()
        spEditor.putString(AppConstant.validFrom, data.validFrom).apply()
        spEditor.putString(AppConstant.validTo, data.validTo).apply()
        spEditor.putInt(AppConstant.tokenType, data.tokenType).apply()
        spEditor.putString(AppConstant.tokenTypeName, data.tokenTypeName)
            .apply()
        spEditor.putString(
            AppConstant.refreshTokenValidTo,
            data.refreshTokenValidTo
        ).apply()

        if(data.tokenTypeName == AppConstant.AccessToken)
            spEditor.putBoolean(AppConstant.IS_LOGGED_IN, true).apply() // mark user as logged in completely...
    }

    private fun storeTenantInfo(tenantConfigurationResponse: TenantConfigurationResponse) {

       AppConstant.userTwoFaSetting = tenantConfigurationResponse.tenantData.userTwoFaSetting
        spEditor.putInt(
            AppConstant.tenantTwoFaSetting,
            tenantConfigurationResponse.tenantData.tenantTwoFaSetting
        ).apply()
    }

    private fun storePhoneInfo(sendTwoFaResponse: SendTwoFaResponse) {

        sendTwoFaResponse.twoFaData?.let { twoFaData->
            twoFaData.phoneNumber?.let {
                spEditor.putString(AppConstant.phoneNumber, it)
                    .apply()
            }
        }

        sendTwoFaResponse.message?.let {
            spEditor.putString(AppConstant.otp_message, sendTwoFaResponse.message)
                .apply()
        }


        sendTwoFaResponse.twoFaData?.let {
            val gson = Gson()
            val otpData = gson.toJson(sendTwoFaResponse.twoFaData)
            spEditor.putString(AppConstant.otpDataJson, otpData).apply()
            spEditor.putInt(AppConstant.secondsCount, 0).apply()
        }

    }


}