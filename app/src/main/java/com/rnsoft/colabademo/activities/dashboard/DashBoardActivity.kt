package com.rnsoft.colabademo


import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.rnsoft.colabademo.databinding.DashboardLayoutBinding
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject


@AndroidEntryPoint
class DashBoardActivity : BaseActivity() {

    private val dashBoardViewModel: DashBoardViewModel by viewModels()

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private lateinit var binding: DashboardLayoutBinding

    private val pageSize = 20
    private val lastId = -1
    private val mediumId = 1

    //private var notificationArrayList: ArrayList<NotificationItem> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DashboardLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppSetting.userHasLoggedIn = true

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_profile,
                R.id.navigation_notifications,
                R.id.navigation_search
            )
        )
        //setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        dashBoardViewModel.notificationCount.observe(this@DashBoardActivity, { count ->
            when {
                count == -1 -> SandbarUtils.showRegular(
                    this@DashBoardActivity,
                    AppConstant.INTERNET_ERR_MSG
                )
                //count == -100 -> SandbarUtils.showRegular(this@DashBoardActivity, "Webservice not responding...")
                count > 0 -> {
                    val badge =
                        navView.getOrCreateBadge(R.id.navigation_notifications) // previously showBadge
                    badge.number = count
                    badge.backgroundColor = getColor(R.color.colaba_red_color)
                    badge.badgeTextColor = getColor(R.color.white)
                }
                count == 0 -> {
                    val badge =
                        navView.getOrCreateBadge(R.id.navigation_notifications) // previously showBadge
                    badge.isVisible = false
                }
                else -> {
                    SandbarUtils.showRegular(
                        this@DashBoardActivity,
                        "Webservice count not responding..."
                    )
                }
            }
        })

        lifecycleScope.launchWhenStarted {
            sharedPreferences.getString(AppConstant.token, "")?.let {
                val count =
                    dashBoardViewModel.getNotificationCountT(it)

                // Also run service for notifications get....
                /*
                dashBoardViewModel.getNotificationListing(
                    token ="it,
                    pageSize = pageSize, lastId = lastId, mediumId = mediumId
                )

                 */
            }
        }

        binding.centerFab.setOnClickListener{
            val startNewApplicationActivity = Intent(this@DashBoardActivity, StartNewApplicationActivity::class.java)
            startActivity(startNewApplicationActivity)
        }
    }




}