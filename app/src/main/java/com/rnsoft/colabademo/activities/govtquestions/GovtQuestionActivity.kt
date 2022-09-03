package com.rnsoft.colabademo

import android.content.SharedPreferences

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.rnsoft.colabademo.databinding.GovtQuestionsActivityLayoutBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import javax.inject.Inject



@AndroidEntryPoint
class GovtQuestionActivity : BaseActivity() {
    @Inject
    lateinit var sharedPreferences: SharedPreferences
    lateinit var binding: GovtQuestionsActivityLayoutBinding
    var loanApplicationId:Int? = null
    private var loanPurpose:String? = null
    var selectedQuestionHeader:String?  =  null
    private var borrowerTabList:ArrayList<Int>? = null
    private var borrowerOwnTypeList:ArrayList<Int>? = null
    private val borrowerApplicationViewModel: BorrowerApplicationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = GovtQuestionsActivityLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold)
        val extras = intent.extras
        extras?.let {
            loanApplicationId = it.getInt(AppConstant.loanApplicationId)
            loanPurpose = it.getString(AppConstant.loanPurpose)
            borrowerTabList = it.getIntegerArrayList(AppConstant.borrowerList) as ArrayList<Int>
            borrowerOwnTypeList = it.getIntegerArrayList(AppConstant.borrowerOwnTypeList) as ArrayList<Int>
            selectedQuestionHeader = it.getString(AppConstant.selectedQuestionHeader).toString()


            //for (item in borrowerTabList!!) Timber.d("item size " + item)
        }

        lifecycleScope.launchWhenStarted {
            coroutineScope {
                sharedPreferences.getString(AppConstant.token, "")?.let { authToken ->
                    if (loanApplicationId != null && borrowerTabList != null && borrowerOwnTypeList != null) {
                        borrowerApplicationViewModel.getGovernmentQuestionsList(authToken, loanApplicationId!!, borrowerOwnTypeList!!, borrowerTabList!!)
                        Timber.e("Government service loaded...Loading DemoGraphic...",loanApplicationId!!, borrowerOwnTypeList!!.get(0), borrowerTabList!!.get(0))
                        //Timber.e("Government service loaded...Loading DemoGraphic...",loanApplicationId!!, borrowerOwnTypeList!!.get(1), borrowerTabList!!.get(1))
                        borrowerApplicationViewModel.getDemoGraphicInfoList(authToken, loanApplicationId!!, borrowerTabList!!)
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
        binding.govtDataLoader.visibility = View.INVISIBLE
        Timber.e("error in service will finish this activity....")
        //finish()
    }


}