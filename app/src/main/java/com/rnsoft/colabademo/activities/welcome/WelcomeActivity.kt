package com.rnsoft.colabademo

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import co.infinum.goldfinger.Goldfinger
import co.infinum.goldfinger.Goldfinger.PromptParams
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class WelcomeActivity : AppCompatActivity() {
    //private val activityScope = CoroutineScope(Dispatchers.Main)
    private lateinit var loginWithTextView: TextView
    private lateinit var withPasswordTextView: TextView
    private lateinit var username: TextView
    private lateinit var greetMessage: TextView
    private lateinit var fingerPrintImageView: ImageView

    private lateinit var goldfinger: Goldfinger

    private lateinit var params: PromptParams


    private var  resumeState =  false

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.welcome_layout)
        greetMessage             =   findViewById(R.id.greetMessage)
        username                =   findViewById(R.id.usernameTextView)
        loginWithTextView       =   findViewById(R.id.loginWithTextView)
        withPasswordTextView    =   findViewById(R.id.withPasswordTextView)
        fingerPrintImageView    =   findViewById(R.id.fingerPrintImage)

        intent.extras?.let {
            resumeState = it.getBoolean(AppSetting.lockAppState)
        }

        sharedPreferences.getString(AppConstant.userName, "Default User")?.let {
            username.text = it
        }

        loginWithTextView.setOnClickListener {
            startActivity(Intent(this@WelcomeActivity, SignUpFlowActivity::class.java))
        }
        withPasswordTextView.setOnClickListener {
            startActivity(Intent(this@WelcomeActivity, SignUpFlowActivity::class.java))
        }

        goldfinger = Goldfinger.Builder(this)
            .logEnabled(true)
            .build()

        params = PromptParams.Builder(this)
            .title(resources.getString(R.string.authenticate_with_biometric))
            .negativeButtonText("Cancel")
            .description(resources.getString(R.string.user_your_biometric))
            //.subtitle("Subtitle")
            .build()

        fingerPrintImageView.setOnClickListener{
            if (goldfinger.canAuthenticate()) {
                goldfinger.authenticate(params , fingerPrintCallBack)
            }
           else
                SandbarUtils.showRegular(this@WelcomeActivity, "Finger Print not available....")
        }

        greetMessage.text = AppSetting.returnGreetingString()
        sharedPreferences.getString(AppConstant.firstName, "")?.let { firstName->
            username.text = firstName
            sharedPreferences.getString(AppConstant.lastName, "")?.let {
                username.text = username.text.toString() +" "+it
            }
        }
    }

    private val fingerPrintCallBack:Goldfinger.Callback = object : Goldfinger.Callback {
        override fun onError(e: Exception) {
           /* Critical error happened */
            SandbarUtils.showError(this@WelcomeActivity, "Finger Print device error....")
        }

        override fun onResult(result: Goldfinger.Result) {
           /* Result received */
            //  || result.type() == Goldfinger.Type.ERROR) {
            if (result.type() == Goldfinger.Type.SUCCESS){
                val formattedResult =
                    String.format("%s - %s", result.type().toString(), result.reason().toString())

                //showToast(formattedResult)
                if(resumeState) {
                    AppSetting.initialScreenLoaded = false
                    finish()
                }
                else {
                    startActivity(Intent(this@WelcomeActivity, DashBoardActivity::class.java))
                    finish()
                }

            }
        }
    }








}