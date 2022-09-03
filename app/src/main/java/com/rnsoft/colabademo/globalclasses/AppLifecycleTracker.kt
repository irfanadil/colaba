package com.rnsoft.colabademo

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log

class AppLifecycleTracker(val appContext:Context) : Application.ActivityLifecycleCallbacks  {

        private var numStarted = 0



        companion object {
            var isApplicationGoneToBackground = false
            var isApplicationInForeground = 0
        }

        override fun onActivityStarted(activity: Activity) {
            if (numStarted == 0)
                Log.e("App Foreground ", "Running$numStarted")
            numStarted++
            isApplicationInForeground = 0

        }

        override fun onActivityStopped(activity: Activity) {
            numStarted--
            isApplicationGoneToBackground = true
            if (numStarted == 0) {
                // app went to background
                Log.e("App background ", "Running$numStarted")

            }
        }

        override fun onActivityResumed(activity: Activity) {



        }
        override fun onActivityPaused(activity: Activity) {}
        override fun onActivityDestroyed(activity: Activity) {}
        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
}