package com.rnsoft.colabademo

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import com.rnsoft.colabademo.databinding.BorrowerAddressLayoutBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.income_military_pay.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject


@AndroidEntryPoint
class BorrowerAddressActivity : BaseActivity() {
    @Inject
    lateinit var sharedPreferences: SharedPreferences
    lateinit var binding: BorrowerAddressLayoutBinding
    private val viewModel : PrimaryBorrowerViewModel by viewModels()
    var loanApplicationId: Int? = null
    var borrowerId: Int? = null
    var isAddBorrower: Boolean? = null
    var borrowerName: String? = null
    var borrowerInfoList: ArrayList<BorrowersInformation> = ArrayList()
    var ownTypeId: Int?= null
    var firstName: String?= null
    var lastName:String ? = null
    var middleName:String? = null

    override fun onCreate(savedInstanceState: Bundle?){

        try {
            val extras = intent.extras
            extras?.let {
                loanApplicationId = it.getInt(AppConstant.loanApplicationId)
                borrowerId = it.getInt(AppConstant.borrowerId)
                isAddBorrower = it.getBoolean(AppConstant.addBorrower)
                borrowerInfoList = it.getParcelableArrayList(AppConstant.coborrowers)!!
                ownTypeId = it.getInt(AppConstant.owntypeid)
                firstName = it.getString(AppConstant.firstName)
                lastName = it.getString(AppConstant.lastName)
                middleName = it.getString(AppConstant.middleName)

                //Log.e("coborrower list", "" + borrowerInfoList)
                //Log.e("owntypeId","borrowerId: " + borrowerId + "OwnTypeId: " + ownTypeId)
                //Log.e("firstName",firstName+ "lastname: " + lastName)
            }
        } catch (e:Exception){
            Log.e("Exception","Primary Activity")
        }

        super.onCreate(savedInstanceState)
        binding = BorrowerAddressLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold)

        lifecycleScope.launchWhenStarted {
            sharedPreferences.getString(AppConstant.token, "")?.let { authToken ->
                if (loanApplicationId != null && borrowerId !=null) {
                    coroutineScope {
                        viewModel.getHousingStatus(authToken)
                        if(isAddBorrower!!){
                            viewModel.refreshBorrowerInfo()
                        } else {
                            binding.loaderInfo.visibility = View.VISIBLE

                            viewModel.getBasicBorrowerDetail(authToken, loanApplicationId!!, borrowerId!!)
                        }

                    }
                }
            }
        }


        val navController = findNavController(R.id.nav_host_borrower_address_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_primary_borrower_info,
                R.id.navigation_current_address,
                R.id.navigation_mailing_address,
                R.id.navigation_active_duty,
                R.id.navigation_reserve,
                R.id.navigation_unmarried,
                R.id.navigation_test_fields,
                R.id.navigation_non_permanent,
            )
        )
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
    fun onLogoutEventReceived(event: LogoutEvent) {
        startActivity(Intent(this@BorrowerAddressActivity, SignUpFlowActivity::class.java))
        finish()
    }

    fun Activity.hideSoftKeyboard() {

    }
}