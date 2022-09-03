package com.rnsoft.colabademo

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class PhoneNumberRepo @Inject
constructor(
    private val phoneNumberRepo : PhoneNumberDataSource,
    @ApplicationContext val applicationContext: Context
) {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    suspend fun skipTwoFactorAuthentication(intermediateToken: String): Result<SkipTwoFactorResponse> {
        return phoneNumberRepo.skipTwoFactorService(intermediateToken)
    }




    /*
    if (otpToNumberResponse.code == "200") {

    }
    else
    if (otpToNumberResponse.code == "400" && otpToNumberResponse.otpData!=null){

    }

     */



}