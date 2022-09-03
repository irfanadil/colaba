package com.rnsoft.colabademo

import android.content.SharedPreferences
import android.util.Log
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TokenAuthenticator ( private val sharedPreferences: SharedPreferences) : Authenticator {

        //override fun authenticate(route: Route?, response: Response): Request? { return null }

        //override fun authenticate(route: Route?, authResponse: Response?): Request? {
        override fun authenticate(route: Route?, response: Response): Request? {
            Log.e("authenticate - ", "Generating Token---")
            val newTokenResponse : LoginResponse = getNewToken() ?: return null
            val newResponseData = newTokenResponse.data
            newResponseData?.let {
                if (it.token.isNotBlank() && it.token.isNotEmpty()) {
                    sharedPreferences.edit().putString(AppConstant.token, it.token)
                        .apply()
                    sharedPreferences.edit().putString(AppConstant.refreshToken, it.refreshToken)
                        .apply()
                    return response.request.newBuilder()
                        .header("Authorization", "Bearer " + it.token)
                        .build()
                }
            }
            return null
        }

        private  fun getNewToken(): LoginResponse?
        {
            // Refresh your access_token using a synchronous api request
            val retrofitInstance = Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val testAPI = retrofitInstance.create(ServerApi::class.java)
            val retrofitResponse = testAPI.refreshToken(
                RefreshTokenRequest( RefreshToken =  AppConstant.refreshToken, Token = AppConstant.token)).execute()
            retrofitResponse.body()?.let {
                it.data?.let { loginData->
                    return retrofitResponse.body()!!
                }
            }
            return null
        }


}