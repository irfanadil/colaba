package com.rnsoft.colabademo.utils

import android.content.Intent
import android.content.SharedPreferences

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager

import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.core.content.ContextCompat
import com.rnsoft.colabademo.AppConstant
import com.rnsoft.colabademo.R
import com.rnsoft.colabademo.SignUpFlowActivity
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.Executor
import javax.inject.Inject


@AndroidEntryPoint
class WelcomeActivityUnused : AppCompatActivity() {
    //private val activityScope = CoroutineScope(Dispatchers.Main)
    private lateinit var loginWithTextView: TextView
    private lateinit var withPasswordTextView: TextView
    private lateinit var username: TextView
    private lateinit var fingerPrintImageView: ImageView

    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private lateinit var executor: Executor

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.welcome_layout)
        username                =   findViewById(R.id.usernameTextView)
        loginWithTextView       =   findViewById(R.id.loginWithTextView)
        withPasswordTextView    =   findViewById(R.id.withPasswordTextView)
        fingerPrintImageView    =   findViewById(R.id.fingerPrintImage)

        sharedPreferences.getString(AppConstant.userName, "Default User")?.let {
            username.text = it
        }

        loginWithTextView.setOnClickListener {
            startActivity(Intent(this@WelcomeActivityUnused, SignUpFlowActivity::class.java))
        }
        withPasswordTextView.setOnClickListener {
            startActivity(Intent(this@WelcomeActivityUnused, SignUpFlowActivity::class.java))
        }




        executor = ContextCompat.getMainExecutor(this)

        biometricPrompt =
            BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    username.text = "Error"
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    username.text = "Success"
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    username.text = "Failure"
                }
            })

            promptInfo = PromptInfo.Builder()
            .setTitle("Programmer World Authentication")
            .setNegativeButtonText("Cancel/ Use Password")
            .setConfirmationRequired(false)
            .build()


            fingerPrintImageView.setOnClickListener(fingerPrintClickListener)







    }


    @RequiresApi(Build.VERSION_CODES.Q)
    val fingerPrintClickListener = View.OnClickListener {
        val biometricManager: BiometricManager = BiometricManager.from(applicationContext)
        if (biometricManager.canAuthenticate() != BiometricManager.BIOMETRIC_SUCCESS) {
            username.text = "Biometric Not Supported"

        }
       else
            biometricPrompt.authenticate(promptInfo)

        /*
        val isHardwarePossible = BiometricUtil.isHardwareAvailable(applicationContext)
        if(isHardwarePossible)
            username.text = "Biometric Hardware Supported"
        else
            username.text = "Biometric NOT EVENT Hardware Supported"

         */

    }










}