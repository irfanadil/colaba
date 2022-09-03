package com.rnsoft.colabademo

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import androidx.lifecycle.ProcessLifecycleOwner
import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import com.google.android.libraries.places.api.Places
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
        ProcessLifecycleOwner.get().lifecycle.addObserver(appLifecycleObserver)
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

}