package com.rnsoft.colabademo

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import androidx.lifecycle.ProcessLifecycleOwner
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.libraries.places.api.Places
import com.rnsoft.colabademo.globalclasses.NewAppLifeCycleObserver
import timber.log.Timber





@HiltAndroidApp
open class ApplicationClass : Application()
{

    companion object{
        var globalAddressList :ArrayList<AddressData> = ArrayList()

    }


    override fun onCreate() {
        super.onCreate()
        //registerActivityLifecycleCallbacks(AppLifecycleTracker(applicationContext))
        val appLifecycleObserver = AppLifecycleObserver(applicationContext)

        // First-Way
        ProcessLifecycleOwner.get().lifecycle.addObserver(appLifecycleObserver)

        // Second-Way
        //ProcessLifecycleOwner.get().lifecycle.addObserver(NewAppLifeCycleObserver(this));

        // Third-Way
        //ProcessLifecycleOwner.get().lifecycle.addObserver(NewAppLifeCycleObserver(this));

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        // Initialize the Places SDK. Note that the string value of `maps_api_key` will be generated
        // at build-time (see app/build.gradle). The technique used here allows you to provide your
        // API key such that the key is not checked in source control.
        //
        // See API Key Best Practices for more information on how to secure your API key:
        // https://developers.google.com/maps/api-key-best-practices
        Places.initialize(this, "AIzaSyBzPEiQOTReBzy6W1UcIyHApPu7_5Die6w")
    }



    var defaultLifecycleObserver = object : DefaultLifecycleObserver {

        override fun onStart(owner: LifecycleOwner) {
            super.onStart(owner)
            if (AppSetting.userHasLoggedIn && !AppSetting.initialScreenLoaded) {
                AppSetting.initialScreenLoaded = true
                if (AppSetting.biometricEnabled) {
                    val intent = Intent(this@ApplicationClass, WelcomeActivity::class.java)
                    intent.putExtra(AppSetting.lockAppState, true)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    this@ApplicationClass.startActivity(intent)
                } else {
                    val intent = Intent(this@ApplicationClass, SignUpFlowActivity::class.java)
                    intent.putExtra(AppSetting.lockAppState, true)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    this@ApplicationClass.startActivity(intent)
                }
            }
        }

        override fun onStop(owner: LifecycleOwner) {
            super.onStop(owner)
            //your code here
        }
    }

}