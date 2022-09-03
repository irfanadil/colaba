package com.rnsoft.colabademo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus


import javax.inject.Inject

/**
 * Created by Anita Kiran on 10/13/2021.
 */
@HiltViewModel
class LoanInfoViewModel @Inject constructor(private val repo: LoanInfoRepo) : ViewModel() {

    private val _loanInfoPurchase : MutableLiveData<LoanInfoDetailsModel> =   MutableLiveData()
    val loanInfoPurchase: LiveData<LoanInfoDetailsModel> get() = _loanInfoPurchase

    private val _loanGoals : MutableLiveData<ArrayList<LoanGoalModel>> =   MutableLiveData()
    val loanGoals: LiveData<ArrayList<LoanGoalModel>> get() = _loanGoals

    suspend fun getLoanInfoPurchase(loanApplicationId:Int) {
        viewModelScope.launch() {
            val responseResult = repo.getLoanInfo(loanApplicationId = loanApplicationId)
            withContext(Dispatchers.Main) {
                if (responseResult is Result.Success)
                    _loanInfoPurchase.value = (responseResult.data)

                else if (responseResult is Result.Error && responseResult.exception.message == AppConstant.INTERNET_ERR_MSG)
                    EventBus.getDefault().post(WebServiceErrorEvent(null, true))
                else if (responseResult is Result.Error)
                    EventBus.getDefault().post(WebServiceErrorEvent(responseResult))
            }
        }
    }

     fun getLoanGoals(loanPurposeId:Int) {
        viewModelScope.launch() {
            val responseResult = repo.getLoanGoals(loanPurposeId = loanPurposeId)
            withContext(Dispatchers.Main) {
                if (responseResult is Result.Success)
                    _loanGoals.value = (responseResult.data)
                else if (responseResult is Result.Error && responseResult.exception.message == AppConstant.INTERNET_ERR_MSG)
                    EventBus.getDefault().post(WebServiceErrorEvent(null, true))
                else if (responseResult is Result.Error)
                    EventBus.getDefault().post(WebServiceErrorEvent(responseResult))
            }
        }
    }

    suspend fun addLoanInfo(data: AddLoanInfoModel) {
        viewModelScope.launch(Dispatchers.IO) {
            val responseResult = repo.addLoanInfo(data)
            withContext(Dispatchers.Main) {
                if(responseResult is Result.Success) {
                    //Log.e("Viewmodel", "${responseResult.data}")
                    //Log.e("Viewmodel", "$responseResult")
                    EventBus.getDefault().post(SendDataEvent(responseResult.data))
                }
                else if (responseResult is Result.Error && responseResult.exception.message == AppConstant.INTERNET_ERR_MSG)
                    EventBus.getDefault().post(WebServiceErrorEvent(null, true))
                else if (responseResult is Result.Error)
                    EventBus.getDefault().post(WebServiceErrorEvent(responseResult))
            }
        }
    }

    suspend fun addLoanRefinanceInfo(data: UpdateLoanRefinanceModel) {
        viewModelScope.launch(Dispatchers.IO) {
            val responseResult = repo.addLoanRefinanceInfo(data)
            withContext(Dispatchers.Main) {
                if(responseResult is Result.Success) {
                    //Log.e("Viewmodel", "${responseResult.data}")
                    //Log.e("Viewmodel", "$responseResult")
                    EventBus.getDefault().post(SendDataEvent(responseResult.data))
                }
                else if (responseResult is Result.Error && responseResult.exception.message == AppConstant.INTERNET_ERR_MSG)
                    EventBus.getDefault().post(WebServiceErrorEvent(null, true))
                else if (responseResult is Result.Error)
                    EventBus.getDefault().post(WebServiceErrorEvent(responseResult))
            }
        }
    }

}