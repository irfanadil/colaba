package com.rnsoft.colabademo

import android.content.SharedPreferences
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject


enum class TOKEN_TYPES() {
    ACCESS_TOKEN ,
    IntermediateToken
}

@HiltViewModel
class LoginViewModel @Inject constructor(private val loginRepo: LoginRepo) :
    ViewModel() {

    //private val _loginResult = MutableStateFlow(LoginResponseResult())
    //val loginResponseResult: SharedFlow<LoginResponseResult> = _loginResult

    //private val _loginResult = MutableFl<LoginResponseResult>()
    //val loginResponseResult: Flow<LoginResponseResult> = Flow()

    @Inject
    lateinit var sharedPreferences: SharedPreferences



    fun login(userEmail: String, password: String) {

        var dontAskTwoFaIdentifier = ""

        val emailError = LoginUtil.isValidEmail(userEmail)
        val passwordLengthError = LoginUtil.checkPasswordLength(password)
        if (emailError != null)
            EventBus.getDefault().post(LoginEvent(LoginResponseResult(emailError = emailError)))
        else if (passwordLengthError != null)
            EventBus.getDefault()
                .post(LoginEvent(LoginResponseResult(passwordError = passwordLengthError)))
        else {
            viewModelScope.launch {

                sharedPreferences.getString(AppConstant.dontAskTwoFaIdentifier ,"")?.let {
                    dontAskTwoFaIdentifier = it
                }

                val genericResult =
                    loginRepo.validateLoginCredentials(userEmail, password,  dontAskTwoFaIdentifier)
                Log.e("login-result - ", genericResult.toString())
                if (genericResult is Result.Success) {
                    val loginResponse = genericResult.data

                    if (loginResponse.data?.tokenTypeName == AppConstant.AccessToken) {
                        EventBus.getDefault().post(LoginEvent(LoginResponseResult(success = loginResponse, screenNumber = 1)))
                        return@launch
                    } else if (loginResponse.data?.tokenTypeName == AppConstant.IntermediateToken) {
                        runOtpSettingService(loginResponse.data.token)
                        //loginRepo.getOtpSettingFromService(loginResponse.data.token)

                        val resultConfiguration =
                            loginRepo.fetchTenantConfiguration(loginResponse.data.token)


                        if (resultConfiguration is Result.Success) {
                            val tenantConfiguration = resultConfiguration.data
                            if (tenantConfiguration.tenantData.tenantTwoFaSetting == 1 ||
                                tenantConfiguration.tenantData.tenantTwoFaSetting == 3
                            ) {
                                val phoneInfoResult =
                                    loginRepo.getPhoneNumberDetail(loginResponse.data.token)

                               if (phoneInfoResult is Result.Success) {
                                    val phoneDetail = phoneInfoResult.data
                                    when (phoneDetail.code) {
                                        "404" -> EventBus.getDefault().post(LoginEvent(LoginResponseResult(success = loginResponse, screenNumber = 2)))
                                        "200" -> EventBus.getDefault().post(LoginEvent(LoginResponseResult(success = loginResponse, screenNumber = 3)))
                                        "400" -> EventBus.getDefault().post(LoginEvent(LoginResponseResult(success = loginResponse, screenNumber = 3)))
                                        "429" -> EventBus.getDefault().post(LoginEvent(LoginResponseResult(success = loginResponse, screenNumber = 3)))
                                        else ->
                                            Log.e("Else", "WebService-error-go")
                                    }

                                }
                            }

                        }
                    }
                }
                else {
                    Log.e("genericResult","$genericResult")
                    if(genericResult is Result.Error && genericResult.exception.message == AppConstant.INTERNET_ERR_MSG)
                        EventBus.getDefault().post(LoginEvent(LoginResponseResult(responseError = AppConstant.INTERNET_ERR_MSG)))
                    else
                        EventBus.getDefault().post(LoginEvent(LoginResponseResult(responseError = "User does not exist")))
                }

            }
        }
    }




    private suspend fun runOtpSettingService(intermediateToken:String){
        loginRepo.otpSettingFromService(intermediateToken)
    }


}

/*
    @ExperimentalCoroutinesApi
    fun newLoginFlow(userEmail: String, password: String) = callbackFlow {
        val genericResult =
            loginRepo.validateLoginCredentials(userEmail, password)
        if (genericResult is Result.Success) {
            val loginResponse = genericResult.data

            if (loginResponse.data?.tokenTypeName == "AccessToken")
                offer(LoginResponseResult(success = loginResponse, screenNumber = 1))
            else if (loginResponse.data?.tokenTypeName == "IntermediateToken") {
                val resultConfiguration =
                    loginRepo.fetchTenantConfiguration(loginResponse.data.token)
                if (resultConfiguration is Result.Success) {
                    val tenantConfiguration = resultConfiguration.data
                    if (tenantConfiguration.tenantData.tenantTwoFaSetting == 1 ||
                        tenantConfiguration.tenantData.tenantTwoFaSetting == 3
                    ) {
                        val phoneInfoResult =
                            loginRepo.getPhoneNumberDetail(loginResponse.data.token)
                        if (phoneInfoResult is Result.Success) {
                            val phoneDetail = phoneInfoResult.data
                            when (phoneDetail.code) {
                                "404" -> offer(
                                    LoginResponseResult(
                                        success = loginResponse,
                                        screenNumber = 2
                                    )
                                )
                                "200" -> offer(
                                    LoginResponseResult(
                                        success = loginResponse,
                                        screenNumber = 3
                                    )
                                )
                                "400" -> offer(
                                    LoginResponseResult(
                                        success = loginResponse,
                                        screenNumber = 3
                                    )
                                )
                                else -> Log.e("Else", "where-to-go")
                            }

                        } else
                            offer(LoginResponseResult(responseError = R.string.login_failed))
                    }

                } else
                    offer(LoginResponseResult(responseError = R.string.login_failed))
            }
        } else
            offer(LoginResponseResult(responseError = R.string.login_failed))
        //close()
        //awaitClose()
    }
 */