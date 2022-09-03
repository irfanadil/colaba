package com.rnsoft.colabademo

import android.content.Intent
import android.content.res.Resources
import androidx.lifecycle.Observer
import android.os.Bundle
import android.util.Log
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFlowActivity : AppCompatActivity() {


    private val signUpFlowViewModel: SignUpFlowViewModel by viewModels()

    private lateinit var appBarConfiguration : AppBarConfiguration

    var  resumeState =  false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signupflow_navigation_layout)

        intent.extras?.let {
            resumeState = it.getBoolean(AppSetting.lockAppState)
            //Log.e("resumeState = ","SignUpFlowActivity = "+resumeState.toString())
        }

        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.signup_nav_host_fragment) as NavHostFragment? ?: return

        // Set up Action Bar
        val navController = host.navController

        appBarConfiguration = AppBarConfiguration(navController.graph)

        setupActionBar(navController, appBarConfiguration)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val dest: String = try {
                resources.getResourceName(destination.id)
            } catch (e: Resources.NotFoundException) {
                destination.id.toString()
            }

            //Log.d("NavigationActivity", "Navigated to $dest")
        }




    }

    private fun setupActionBar(navController: NavController,
                               appBarConfig : AppBarConfiguration
    ) {
        // TODO STEP 9.6 - Have NavigationUI handle what your ActionBar displays
//        // This allows NavigationUI to decide what label to show in the action bar
//        // By using appBarConfig, it will also determine whether to
//        // show the up arrow or drawer menu icon
//        setupActionBarWithNavController(navController, appBarConfig)
        // TODO END STEP 9.6
    }



}


