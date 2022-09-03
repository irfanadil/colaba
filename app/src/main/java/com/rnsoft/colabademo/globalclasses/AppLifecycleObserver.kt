package com.rnsoft.colabademo

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.LifecycleObserver

class AppLifecycleObserver(val appContext: Context) : LifecycleObserver {



        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        fun onEnterForeground() {
            //run the code we need
            if (AppSetting.userHasLoggedIn && !AppSetting.initialScreenLoaded) {
                AppSetting.initialScreenLoaded = true
                if (AppSetting.biometricEnabled) {
                    val intent = Intent(appContext, WelcomeActivity::class.java)
                    intent.putExtra(AppSetting.lockAppState, true)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    appContext.startActivity(intent)
                } else {
                    val intent = Intent(appContext, SignUpFlowActivity::class.java)
                    intent.putExtra(AppSetting.lockAppState, true)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    appContext.startActivity(intent)
                }
            }
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        fun onEnterBackground() {
            //run the code we need
            //AppSetting.lockAppState = true
        }

        companion object {
            val TAG = AppLifecycleObserver::class.java.name
        }
    }
