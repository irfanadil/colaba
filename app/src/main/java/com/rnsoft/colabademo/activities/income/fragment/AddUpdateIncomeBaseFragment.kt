package com.rnsoft.colabademo

import android.content.SharedPreferences
import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.rnsoft.colabademo.activities.addresses.info.fragment.DeleteCurrentResidenceDialogFragment
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

/**
 * Created by Anita Kiran on 11/17/2021.
 */

open class AddUpdateIncomeBaseFragment : BaseFragment(){
    protected var loanApplicationId:Int? = null
    protected var loanPurpose:String? = null
    protected var borrowerId:Int? = null
    @Inject
    lateinit var sharedPreferences: SharedPreferences
    protected val viewModel: IncomeViewModel by activityViewModels()



    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onIncomeDeleteReceived(evt: IncomeDeleteEvent) {
        if(evt.isDeleteIncome){
            Log.e("incomedelete", "called")
            /*if (loanApplicationId != null && borrowerId != null && borrowerAssetId >0) {
                viewModel.genericAddUpdateAssetResponse.observe(viewLifecycleOwner, { genericAddUpdateAssetResponse ->
                    val codeString = genericAddUpdateAssetResponse.code.toString()
                    if(codeString == "400"){
                        //updateMainAsset()
                        findNavController().popBackStack()
                    }
                })

                lifecycleScope.launchWhenStarted {
                    sharedPreferences.getString(AppConstant.token, "")?.let { authToken ->
                        viewModel.deleteAsset(authToken, borrowerAssetId, borrowerId!!, loanApplicationId!!)
                    }
                }
            } */
        }
    }
}