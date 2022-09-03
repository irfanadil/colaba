package com.rnsoft.colabademo

import android.content.Intent
import android.content.SharedPreferences

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.rnsoft.colabademo.databinding.AssetsActivityLayoutBinding
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class AssetsActivity : BaseActivity() {
    @Inject
    lateinit var sharedPreferences: SharedPreferences
    lateinit var binding: AssetsActivityLayoutBinding

    var loanApplicationId:Int? = null
    var loanPurpose:String? = null
    var borrowerTabList:ArrayList<Int>? = arrayListOf()
    private val borrowerApplicationViewModel: BorrowerApplicationViewModel by viewModels()

    private val assetViewModel: AssetViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AssetsActivityLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold)

        val extras = intent.extras
        extras?.let {
            loanApplicationId = it.getInt(AppConstant.loanApplicationId)
            loanPurpose = it.getString(AppConstant.loanPurpose)
            borrowerTabList = it.getIntegerArrayList(AppConstant.assetBorrowerList) as ArrayList<Int>
            //borrowerTabList!!.add(5)
            for(item in borrowerTabList!!){
                Timber.d("item size "+ item)
            }
            Timber.d("borrowerTabList size "+ borrowerTabList!!.size)

            lifecycleScope.launchWhenStarted {
                sharedPreferences.getString(AppConstant.token, "")?.let { authToken ->
                    if(loanApplicationId != null && borrowerTabList != null && loanApplicationId!=null ) {
                        borrowerApplicationViewModel.getBorrowerWithAssets(
                            authToken, loanApplicationId!!,
                            borrowerTabList!!
                        )
                        // pre-fetch bank drop down  values....
                        assetViewModel.getBankAccountType(authToken)

                        // pre-fetch gift drop down values....
                        assetViewModel.getAllGiftSources(authToken)

                        // pre-fetch gift drop down values....
                        assetViewModel.getAllFinancialAsset(authToken)

                        // pre-fetch other drop down values.... Other
                        //assetViewModel.get(authToken)

                    }
                }
            }
        }
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
    fun onErrorEvent(event: WebServiceErrorEvent) {
        binding.assetDataLoader.visibility = View.INVISIBLE
        SandbarUtils.showError(this, AppConstant.WEB_SERVICE_ERR_MSG )
        //finish()

    }
}