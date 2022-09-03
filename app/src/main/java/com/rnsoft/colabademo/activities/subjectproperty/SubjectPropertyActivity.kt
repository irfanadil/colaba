package com.rnsoft.colabademo

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.rnsoft.colabademo.databinding.BorrowerSubjectPropertyLayoutBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class SubjectPropertyActivity : BaseActivity() {
    @Inject
    lateinit var sharedPreferences : SharedPreferences
    lateinit var binding : BorrowerSubjectPropertyLayoutBinding
    private val viewModel : BorrowerApplicationViewModel by viewModels()
    private val subPropertyViewModel : SubjectPropertyViewModel by viewModels()
    var purpose : String? = null
    var loanApplicationId: Int? = null
    var loanPurpose:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = BorrowerSubjectPropertyLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold)

        val extras = intent.extras
        extras?.let {
            loanApplicationId = it.getInt(AppConstant.loanApplicationId)
            purpose = it.getString(AppConstant.borrowerPurpose)
        }

        val navController = findNavController(R.id.nav_host_borrower_subject_property)
        lifecycleScope.launchWhenStarted {
            sharedPreferences.getString(AppConstant.token, "")?.let { authToken ->
                if (loanApplicationId != null) {
                    //Log.e("loanApplicationId",""+loanApplicationId)
                    coroutineScope {
                        binding.loaderSubjectProperty.visibility = View.VISIBLE
                        val call1 = async { viewModel.getPropertyTypes(authToken) }
                        val call2 = async { viewModel.getOccupancyType(authToken) }
                        val call3 = async { subPropertyViewModel.getCoBorrowerOccupancyStatus(authToken, loanApplicationId!!) }
                        if (purpose.equals(AppConstant.purchase, ignoreCase = true)) {
                            val call4 = async {
                                viewModel.getSubjectPropertyDetails(authToken, loanApplicationId!!)
                            }
                            call4.await()
                            navController.navigate(R.id.nav_sub_property_purchase)

                        } else if (purpose.equals(AppConstant.refinance, ignoreCase = true)) {
                            val call4 = async {
                                viewModel.getRefinanceDetails(authToken, loanApplicationId!!)
                            }
                            call4.await()
                            navController.navigate(R.id.nav_sub_property_refinance)
                        }
                    }
                }
            }
        }
    }
 }