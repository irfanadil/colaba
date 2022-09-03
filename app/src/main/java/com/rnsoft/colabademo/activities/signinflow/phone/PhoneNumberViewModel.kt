package com.rnsoft.colabademo

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rnsoft.colabademo.activities.signinflow.phone.events.SkipEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject


@HiltViewModel
class PhoneNumberViewModel @Inject constructor(private val phoneNumberRepo: PhoneNumberRepo) :
    ViewModel() {


    @Inject
    lateinit var sharedPreferences: SharedPreferences

    fun skipTwoFactor() {
        viewModelScope.launch {
            val result = sharedPreferences.getString(AppConstant.token, "")?.let {
                phoneNumberRepo.skipTwoFactorAuthentication(
                    it
                )
            }
            result?.let {
                if (result is Result.Success) {
                        EventBus.getDefault().post(SkipEvent(result.data))
                }
                else if(result is Result.Error && result.exception.message == AppConstant.INTERNET_ERR_MSG)
                    EventBus.getDefault().post(SkipEvent(SkipTwoFactorResponse(AppConstant.INTERNET_ERR_CODE, null, AppConstant.INTERNET_ERR_MSG, null)))
                else
                    EventBus.getDefault().post(SkipEvent(SkipTwoFactorResponse("300", null, "Webservice error, can not skip", null)))
            }

        }
    }




}