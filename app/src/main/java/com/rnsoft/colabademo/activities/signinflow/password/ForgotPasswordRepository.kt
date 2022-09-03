package com.rnsoft.colabademo

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ForgotPasswordRepository @Inject
constructor(
    private val passwordDataSource: ForgotPasswordDataSource,
    @ApplicationContext val applicationContext: Context
) {
    suspend fun sendForgotPasswordEmail(userEmail: String): Result<ForgotPasswordResponse> {
        return passwordDataSource.forgotPasswordService(userEmail)
    }
}