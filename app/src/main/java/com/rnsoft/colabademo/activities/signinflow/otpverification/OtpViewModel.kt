package com.rnsoft.colabademo

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject


@HiltViewModel
class OtpViewModel @Inject constructor(private val otpRepo: OtpRepo) :
    ViewModel() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    fun verifyOtp(otpCode:Int) {
        sharedPreferences.getString(AppConstant.token, "")?.let { intermediateToken ->
            sharedPreferences.getString(AppConstant.phoneNumber, "")?.let { phoneNumber ->
                viewModelScope.launch {
                    val genericResult = otpRepo.startOtpVerification(intermediateToken, phoneNumber, otpCode )
                    if (genericResult is Result.Success) {
                        EventBus.getDefault().post( OtpVerificationEvent(genericResult.data))
                    }
                    else
                    if(genericResult is Result.Error && genericResult.exception.message == AppConstant.INTERNET_ERR_MSG)
                        EventBus.getDefault().post(OtpVerificationEvent(OtpVerificationResponse(AppConstant.INTERNET_ERR_CODE, null, AppConstant.INTERNET_ERR_MSG, null)))
                    else
                        EventBus.getDefault().post(OtpVerificationEvent(OtpVerificationResponse("600", null, "Web service error...", null)))
                }
            }
        }
    }

    fun notAskForOtp(token:String){
        sharedPreferences.getString(AppConstant.token, "")?.let {
            viewModelScope.launch {
                val genericResult = otpRepo.notAskForOtpAgain(token)
                if (genericResult is Result.Success) {
                    EventBus.getDefault().post(NotAskForOtpEvent(genericResult.data))
                }
                else
                if(genericResult is Result.Error && genericResult.exception.message == AppConstant.INTERNET_ERR_MSG)
                    EventBus.getDefault().post(NotAskForOtpEvent(NotAskForOtpResponse(AppConstant.INTERNET_ERR_CODE, null, AppConstant.INTERNET_ERR_MSG, null)))
                else
                    EventBus.getDefault().post(NotAskForOtpEvent(NotAskForOtpResponse("600",null,"Web service error...", null)))
            }
        }
    }


}