package com.rnsoft.colabademo.globalclasses

import android.content.Context
import android.content.Intent
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.rnsoft.colabademo.AppLifecycleObserver
import com.rnsoft.colabademo.AppSetting
import com.rnsoft.colabademo.SignUpFlowActivity
import com.rnsoft.colabademo.WelcomeActivity


class NewAppLifeCycleObserver(private val appContext: Context): DefaultLifecycleObserver {

    override fun onResume(owner: LifecycleOwner) {
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

    override fun onDestroy(owner: LifecycleOwner) {

    }

    companion object {
        val TAG = NewAppLifeCycleObserver::class.java.name
    }
}