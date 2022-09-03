package com.rnsoft.colabademo

import android.content.SharedPreferences
import android.util.Log
import android.util.Patterns

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rnsoft.colabademo.activities.signinflow.phone.events.OtpSentEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@HiltViewModel
class SignUpFlowViewModel @Inject constructor(private val signUpFlowRepository: SignUpFlowRepository) : ViewModel() {

    //private val _loginResult = MutableLiveData<LoginResponseResult>()
    //val loginResponseResult: LiveData<LoginResponseResult> = _loginResult

    fun sendOtpToPhone(intermediateToken:String, phoneNumber: String) {
        var correctPhoneNumber = phoneNumber.replace(" ", "")
        correctPhoneNumber = correctPhoneNumber.replace("+1", "")
        correctPhoneNumber = correctPhoneNumber.replace("-", "")
        correctPhoneNumber = correctPhoneNumber.replace("(", "")
        correctPhoneNumber = correctPhoneNumber.replace(")", "")

        viewModelScope.launch {
        val result = signUpFlowRepository.sendOtpToPhone(intermediateToken, correctPhoneNumber)
            if (result is Result.Success)
                EventBus.getDefault().post(OtpSentEvent(result.data))
            else if(result is Result.Error && result.exception.message == AppConstant.INTERNET_ERR_MSG)
                EventBus.getDefault().post(OtpSentEvent(OtpSentResponse(AppConstant.INTERNET_ERR_CODE, null, AppConstant.INTERNET_ERR_MSG, null)))
            else
                EventBus.getDefault().post(OtpSentEvent(OtpSentResponse("300", null, "Webservice error, can not skip", null)))
        }
    }

}