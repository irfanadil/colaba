package com.rnsoft.colabademo

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.rnsoft.colabademo.databinding.RealEstateActivityLayoutBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import javax.inject.Inject

/**
 * Created by Anita Kiran on 9/16/2021.
 */
@AndroidEntryPoint
class RealEstateActivity : BaseActivity() {
    @Inject
    lateinit var sharedPreferences: SharedPreferences
    lateinit var binding: RealEstateActivityLayoutBinding
    private val viewModel : RealEstateViewModel by viewModels()
    var loanApplicationId: Int? =null
    var borrowerPropertyId : Int? = null
    var borrowerId : Int? = null
    var propertyInfoId : Int? = null
    var borrowerName: String? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        val extras = intent.extras
        extras?.let {
            loanApplicationId = it.getInt(AppConstant.loanApplicationId)
            borrowerPropertyId = it.getInt(AppConstant.borrowerPropertyId)
            borrowerId = it.getInt(AppConstant.borrowerId)
            propertyInfoId = it.getInt(AppConstant.propertyInfoId)
            borrowerName = it.getString(AppConstant.borrowerName)
        }
        super.onCreate(savedInstanceState)
        binding = RealEstateActivityLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        overridePendingTransition(R.anim.slide_up, R.anim.hold)



        //Log.e("activity","loanApplicatioId: " + loanApplicationId + " borrowerPropertyId:" + borrowerPropertyId + " borrowerId: " + borrowerId)

        lifecycleScope.launchWhenStarted {
            sharedPreferences.getString(AppConstant.token, "")?.let { authToken ->
                borrowerPropertyId?.let { id->
                    if (loanApplicationId != null) {
                        coroutineScope {
                            binding.loaderRealEstate.visibility = View.VISIBLE

                            if(borrowerPropertyId!! >0) {
                                viewModel.getRealEstateDetails(authToken, loanApplicationId!!, borrowerPropertyId!!)
                            }
                            //viewModel.getFirstMortgageDetails(authToken, loanApplicationId!!, borrowerPropertyId!!)
                            //viewModel.getSecondMortgageDetails(authToken, loanApplicationId!! 1003)
                            viewModel.getPropertyTypes(authToken)
                            viewModel.getOccupancyType(authToken)
                            viewModel.getPropertyStatus(authToken)
                        }
                    }
                }
            }
        }
    }
}