package com.rnsoft.colabademo

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject


@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(private val forgotPasswordRepo: ForgotPasswordRepository) :
    ViewModel() {

    //val _forgotPasswordResponse = MutableLiveData<ForgotPasswordResponse>()
    //val forgotPasswordResponse: LiveData<ForgotPasswordResponse>? = _forgotPasswordResponse

    fun forgotPassword(userEmail: String) {
        val emailPreCheck  = isValidEmail(userEmail)
        if ( emailPreCheck != null)
           // _forgotPasswordResponse.value = ForgotPasswordResponse("600", null, emailPreCheck, null)
        EventBus.getDefault().post(ForgotPasswordEvent(ForgotPasswordResponse("600", null, emailPreCheck, null)))
        else {
            viewModelScope.launch {
                val genericResult = forgotPasswordRepo.sendForgotPasswordEmail(userEmail)
                if (genericResult is Result.Success) {
                    //_forgotPasswordResponse.value = genericResult.data
                    EventBus.getDefault().post(ForgotPasswordEvent(genericResult.data))

                }
                else if(genericResult is Result.Error && genericResult.exception.message == AppConstant.INTERNET_ERR_MSG)
                        EventBus.getDefault().post(ForgotPasswordEvent(ForgotPasswordResponse(AppConstant.INTERNET_ERR_CODE, null, AppConstant.INTERNET_ERR_MSG, null)))
                else
                    EventBus.getDefault().post(ForgotPasswordEvent(ForgotPasswordResponse("600", null, "User does not exist", null)))
                    //_forgotPasswordResponse.value =ForgotPasswordResponse("300", null, "User does not exist...", null)
            }
        }
    }

    private fun isValidEmail(userEmail: String): String? {
        if (userEmail.isNotBlank()) {
            if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches())
                return "Invalid Email, Please try again…"
        } else
            return "Empty Email, Please try again…"
        return null
    }

}