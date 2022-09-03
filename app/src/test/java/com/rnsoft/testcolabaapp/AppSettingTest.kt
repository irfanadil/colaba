package com.rnsoft.testcolabaapp

import com.google.common.truth.Truth
import com.rnsoft.colabademo.AppSetting
import com.rnsoft.colabademo.LoginUtil
import org.junit.Test

import org.junit.Assert.*

class AppSettingTest {

    @Test
    fun returnGreetingString() {
        val result = AppSetting.returnGreetingString()
        Truth.assertThat(result).isEqualTo(AppSetting.returnGreetingString())
    }


    @Test
    fun lastseen(){
        val result = AppSetting.lastseen(System.currentTimeMillis())
        Truth.assertThat(result).isEqualTo("just now")
    }

}