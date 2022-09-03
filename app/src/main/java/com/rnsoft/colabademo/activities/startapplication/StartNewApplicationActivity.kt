package com.rnsoft.colabademo

import android.content.SharedPreferences
import android.os.Bundle
import com.rnsoft.colabademo.databinding.ActivityStartNewApplicationBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Created by Anita Kiran on 9/17/2021.
 */

@AndroidEntryPoint
class StartNewApplicationActivity : BaseActivity() {
    @Inject
    lateinit var sharedPreferences : SharedPreferences
    private lateinit var binding : ActivityStartNewApplicationBinding
    var purpose : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartNewApplicationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold)

    }
}