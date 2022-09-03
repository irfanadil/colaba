package com.rnsoft.colabademo

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.rnsoft.colabademo.databinding.BorrowerLoanLayoutBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class BorrowerLoanActivity : BaseActivity() {
    @Inject
    lateinit var sharedPreferences: SharedPreferences
    lateinit var binding: BorrowerLoanLayoutBinding
    private val viewModel: LoanInfoViewModel by viewModels()

    var loanApplicationId: Int? = null
    var loanPurpose: String? = null

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = BorrowerLoanLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold)

        val extras = intent.extras
        extras?.let {
            loanApplicationId = it.getInt(AppConstant.loanApplicationId)
            loanPurpose = it.getString(AppConstant.loanPurpose)
        }

        val navController = findNavController(R.id.nav_host_borrower_loan)

        lifecycleScope.launchWhenStarted {

            if (loanApplicationId != null) {
                //Log.e("authToken", authToken) //Log.e("****laon id", "" + loanApplicationId)
                coroutineScope {
                    binding.loaderLoanInfo.visibility = View.VISIBLE
                    viewModel.getLoanInfoPurchase(loanApplicationId!!)
                    if (loanPurpose.equals(AppConstant.purchase, ignoreCase = true)) {
                        val call1 = async {
                            viewModel.getLoanGoals(AppConstant.PURPOSE_ID_PURCHASE)
                        }
                        call1.await()
                        navController.navigate(R.id.navigation_loan_purchase)
                    } else if (loanPurpose.equals(AppConstant.refinance, ignoreCase = true)) {
                        val call = async {
                            viewModel.getLoanGoals(AppConstant.PURPOSE_ID_REFINANCE)
                        }
                        call.await()
                        navController.navigate(R.id.navigation_loan_refinance)
                    }
                }
            }
        }
    }
}