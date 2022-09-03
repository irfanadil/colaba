package com.rnsoft.colabademo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@HiltViewModel
class StartNewAppViewModel @Inject constructor(private val startNewAppRepo: StartNewAppRepo ) :
    ViewModel() {

    private val _searchResultResponse : MutableLiveData<SearchResultResponse> =   MutableLiveData()
    val searchResultResponse: LiveData<SearchResultResponse> get() = _searchResultResponse

    private val _lookUpBorrowerContactResponse : MutableLiveData<LookUpBorrowerContactResponse> =   MutableLiveData()
    val lookUpBorrowerContactResponse: LiveData<LookUpBorrowerContactResponse> get() = _lookUpBorrowerContactResponse

    private val _createNewAppResponse : MutableLiveData<CreateNewApplicationResponse> =   MutableLiveData()
    val createNewAppResponse: LiveData<CreateNewApplicationResponse> get() = _createNewAppResponse

    private val _getLoanOfficerResponse : MutableLiveData<LoanOfficerApiResponse> =   MutableLiveData()
    val getLoanOfficerResponse: LiveData<LoanOfficerApiResponse> get() = _getLoanOfficerResponse

    private val _createNewApplicationParams : MutableLiveData<CreateNewApplicationParams> =   MutableLiveData()
    val createNewApplicationParams: LiveData<CreateNewApplicationParams> get() = _createNewApplicationParams


     val _mcu : MutableLiveData<Mcu> =   MutableLiveData()
    val mcu: LiveData<Mcu> get() = _mcu

    fun setCreateNewParams(createNewApplicationParams:CreateNewApplicationParams){
        _createNewApplicationParams.value = createNewApplicationParams
    }

    suspend fun searchByBorrowerContact(token:String, searchKeyword:String){
        viewModelScope.launch(Dispatchers.IO) {
            val result = startNewAppRepo.searchByBorrowerContact(token = token, searchKeyword = searchKeyword)
            withContext(Dispatchers.Main) {
                if (result is Result.Success)
                    _searchResultResponse.value = result.data
                else if (result is Result.Error && result.exception.message == AppConstant.INTERNET_ERR_MSG)
                    EventBus.getDefault().post(WebServiceErrorEvent(null, true))
                else if (result is Result.Error)
                    EventBus.getDefault().post(WebServiceErrorEvent(result))
            }
        }
    }

    suspend fun lookUpBorrowerContact(token:String, borrowerEmail:String, borrowerPhone:String?=null){
        viewModelScope.launch(Dispatchers.IO) {
            val result = startNewAppRepo.lookUpBorrowerContact(token = token, borrowerEmail = borrowerEmail, borrowerPhone = borrowerPhone)
            withContext(Dispatchers.Main) {
                if (result is Result.Success)
                    _lookUpBorrowerContactResponse.value = result.data
                else if (result is Result.Error && result.exception.message == AppConstant.INTERNET_ERR_MSG)
                    EventBus.getDefault().post(WebServiceErrorEvent(null, true))
                else if (result is Result.Error)
                    EventBus.getDefault().post(WebServiceErrorEvent(result))
            }
        }
    }

    suspend fun createApplication(token:String, createNewApplicationParams: CreateNewApplicationParams){
        viewModelScope.launch(Dispatchers.IO) {
            val result = startNewAppRepo.createApplication(token = token, createNewApplicationParams = createNewApplicationParams)
            withContext(Dispatchers.Main) {
                if (result is Result.Success)
                    _createNewAppResponse.value = result.data
                else if (result is Result.Error && result.exception.message == AppConstant.INTERNET_ERR_MSG)
                    EventBus.getDefault().post(WebServiceErrorEvent(null, true))
                else if (result is Result.Error)
                    EventBus.getDefault().post(WebServiceErrorEvent(result))
            }
        }
    }


    suspend fun getMcusByRoleId(token:String, filterLoanOfficer:Boolean){
        viewModelScope.launch(Dispatchers.IO) {
            val result = startNewAppRepo.getMcusByRoleId(token = token, filterLoanOfficer= filterLoanOfficer)
            withContext(Dispatchers.Main) {
                if (result is Result.Success)
                    _getLoanOfficerResponse.value = result.data
                else if (result is Result.Error && result.exception.message == AppConstant.INTERNET_ERR_MSG)
                    EventBus.getDefault().post(WebServiceErrorEvent(null, true))
                else if (result is Result.Error)
                    EventBus.getDefault().post(WebServiceErrorEvent(result))
            }
        }
    }

    fun setMcu(mcu:Mcu){
        _mcu.value = mcu
        _mcu.postValue(mcu)
    }

}