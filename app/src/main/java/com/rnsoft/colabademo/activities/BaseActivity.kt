package com.rnsoft.colabademo

import androidx.appcompat.app.AppCompatActivity

open class BaseActivity: AppCompatActivity() {
    override fun onResume() {
        super.onResume()
        //AppSetting.userHasLoggedIn = true // open this to enable the security check in.....
        AppSetting.userHasLoggedIn = false  // for testing...
    }

}