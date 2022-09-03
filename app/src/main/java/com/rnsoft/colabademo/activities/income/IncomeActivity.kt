package com.rnsoft.colabademo


import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.rnsoft.colabademo.databinding.IncomeActivityLayoutBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class IncomeActivity : BaseActivity() {
    @Inject
    lateinit var sharedPreferences: SharedPreferences
    lateinit var binding: IncomeActivityLayoutBinding
    private val viewModel: BorrowerApplicationViewModel by viewModels()
    private val incomeViewModel: IncomeViewModel by viewModels()
    var borrowerTabList:ArrayList<Int>? = null
    var loanApplicationId:Int? = null
    var loanPurpose:String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = IncomeActivityLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold)

        val extras = intent.extras
        extras?.let {
            loanApplicationId = it.getInt(AppConstant.loanApplicationId)
            loanPurpose = it.getString(AppConstant.loanPurpose)
            borrowerTabList = it.getIntegerArrayList(AppConstant.incomeBorrowerList) as ArrayList<Int>
            /*for (item in borrowerTabList!!) {
                //Timber.d("item size " + item)
            } */

            lifecycleScope.launchWhenStarted {
                sharedPreferences.getString(AppConstant.token, "")?.let { authToken ->
                    if (loanApplicationId != null && borrowerTabList != null && loanApplicationId != null)
                        viewModel.getBorrowerWithIncome(authToken,  loanApplicationId!!, borrowerTabList!!)
                    incomeViewModel.getRetirementIncomeTypes(authToken)
                    incomeViewModel.getOtherIncomeTypes(authToken)
                    incomeViewModel.getBusinessTypes(authToken)
                }
            }
        }



        /*
        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_asset) as NavHostFragment? ?: return
        // Set up Action Bar
        val navController = host.navController
        appBarConfiguration = AppBarConfiguration(navController.graph)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val dest: String = try {
                resources.getResourceName(destination.id)
            } catch (e: Resources.NotFoundException) {
                destination.id.toString()
            }
            //Log.d("NavigationActivity", "Navigated to $dest")
        }
        */

    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onErrorEvent(webEvent: WebServiceErrorEvent) {
        binding.incomeDataLoader.visibility = View.INVISIBLE
        //finish()
    }

}