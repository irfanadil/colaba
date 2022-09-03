package com.rnsoft.colabademo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rnsoft.AssetTypesByCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

/**
 * Created by Anita Kiran on 11/1/2021.
 */

@HiltViewModel
class IncomeViewModel @Inject constructor(private val repo: IncomeRepo) : ViewModel() {

    private val _assetByCategory: MutableLiveData<ArrayList<AssetTypesByCategory>> =
        MutableLiveData()
    val assetByCategory: LiveData<ArrayList<AssetTypesByCategory>> get() = _assetByCategory

    private val _employmentDetail: MutableLiveData<EmploymentDetailResponse?> = MutableLiveData()
    val employmentDetail: LiveData<EmploymentDetailResponse?> get() = _employmentDetail

    private val _prevEmploymentDetail: MutableLiveData<EmploymentDetailResponse?> = MutableLiveData()
    val prevEmploymentDetail: LiveData<EmploymentDetailResponse?> get() = _prevEmploymentDetail

    private val _selfEmploymentDetail: MutableLiveData<SelfEmploymentResponse?> = MutableLiveData()
    val selfEmploymentDetail: LiveData<SelfEmploymentResponse?> get() = _selfEmploymentDetail

    private val _businessIncomeData: MutableLiveData<BusinessIncomeResponse?> = MutableLiveData()
    val businessIncome: LiveData<BusinessIncomeResponse?> get() = _businessIncomeData

    private val _militaryIncomeData: MutableLiveData<MilitaryIncomeResponse?> = MutableLiveData()
    val militaryIncomeData: LiveData<MilitaryIncomeResponse?> get() = _militaryIncomeData

    private val _retirementIncomeData: MutableLiveData<RetirementIncomeResponse?> = MutableLiveData()
    val retirementIncomeData: LiveData<RetirementIncomeResponse?> get() = _retirementIncomeData

    private val _retirementIncomeTypes: MutableLiveData<ArrayList<DropDownResponse>> =
        MutableLiveData()
    val retirementIncomeTypes: LiveData<ArrayList<DropDownResponse>> get() = _retirementIncomeTypes

    private val _otherIncomeTypes: MutableLiveData<ArrayList<DropDownResponse>> = MutableLiveData()
    val otherIncomeTypes: LiveData<ArrayList<DropDownResponse>> get() = _otherIncomeTypes

    private val _businessTypes: MutableLiveData<ArrayList<DropDownResponse>> = MutableLiveData()
    val businessTypes: LiveData<ArrayList<DropDownResponse>> get() = _businessTypes

    private val _otherIncomeData: MutableLiveData<OtherIncomeResponse?> = MutableLiveData()
    val otherIncomeData: LiveData<OtherIncomeResponse?> get() = _otherIncomeData

    private val _addUpdateIncomeResponse: MutableLiveData<GenericAddUpdateAssetResponse?> = MutableLiveData()
    val addUpdateIncomeResponse: LiveData<GenericAddUpdateAssetResponse?> get() = _addUpdateIncomeResponse

    fun resetChildFragmentToNull(){
        _employmentDetail.value = null
        _employmentDetail.postValue(null)

        _prevEmploymentDetail.value = null
        _prevEmploymentDetail.postValue(null)

        _selfEmploymentDetail.value = null
        _selfEmploymentDetail.postValue(null)

        _businessIncomeData.value = null
        _businessIncomeData.postValue(null)

        _militaryIncomeData.value = null
        _militaryIncomeData.postValue(null)

        _retirementIncomeData.value = null
        _retirementIncomeData.postValue(null)

        _otherIncomeData.value = null
        _otherIncomeData.postValue(null)

        _addUpdateIncomeResponse.value  = null
        _addUpdateIncomeResponse.postValue(null)

    }

    suspend fun getEmploymentDetail(
        token: String,
        loanApplicationId: Int,
        borrowerId: Int,
        incomeInfoId: Int
    ) {
        //delay(1000)
        viewModelScope.launch(Dispatchers.IO) {
            val responseResult = repo.getEmploymentDetails(
                token = token,
                loanApplicationId = loanApplicationId,
                borrowerId, incomeInfoId
            )
            withContext(Dispatchers.Main) {
                if (responseResult is Result.Success)
                    _employmentDetail.value = (responseResult.data)
                else if (responseResult is Result.Error && responseResult.exception.message == AppConstant.INTERNET_ERR_MSG)
                    EventBus.getDefault().post(WebServiceErrorEvent(null, true))
                else if (responseResult is Result.Error)
                    EventBus.getDefault().post(WebServiceErrorEvent(responseResult))
            }
        }
    }

    suspend fun getPrevEmploymentDetail(
        token: String,
        loanApplicationId: Int,
        borrowerId: Int,
        incomeInfoId: Int
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val responseResult = repo.getEmploymentDetails(
                token = token,
                loanApplicationId = loanApplicationId,
                borrowerId, incomeInfoId
            )
            withContext(Dispatchers.Main) {
                if (responseResult is Result.Success)
                    _prevEmploymentDetail.value = (responseResult.data)
                else if (responseResult is Result.Error && responseResult.exception.message == AppConstant.INTERNET_ERR_MSG)
                    EventBus.getDefault().post(WebServiceErrorEvent(null, true))
                else if (responseResult is Result.Error)
                    EventBus.getDefault().post(WebServiceErrorEvent(responseResult))
            }
        }
    }

    suspend fun getSelfEmploymentDetail(token: String, borrowerId: Int, incomeInfoId: Int) {
        //delay(1000)
        viewModelScope.launch(Dispatchers.IO) {
            val responseResult = repo.getSelfEmploymentData(
                token = token,
                borrowerId,
                incomeInfoId
            )
            withContext(Dispatchers.Main) {
                if (responseResult is Result.Success)
                    _selfEmploymentDetail.value = (responseResult.data)
                else if (responseResult is Result.Error && responseResult.exception.message == AppConstant.INTERNET_ERR_MSG)
                    EventBus.getDefault().post(WebServiceErrorEvent(null, true))
                else if (responseResult is Result.Error)
                    EventBus.getDefault().post(WebServiceErrorEvent(responseResult))
            }
        }
    }

    suspend fun getIncomeBusiness(token: String, borrowerId: Int, incomeInfoId: Int) {
        //delay(1000)
        viewModelScope.launch(Dispatchers.IO) {
            val responseResult = repo.getBusinessIncome(
                token = token,
                borrowerId,
                incomeInfoId
            )
            withContext(Dispatchers.Main) {
                if (responseResult is Result.Success)
                    _businessIncomeData.value = (responseResult.data)
                else if (responseResult is Result.Error && responseResult.exception.message == AppConstant.INTERNET_ERR_MSG)
                    EventBus.getDefault().post(WebServiceErrorEvent(null, true))
                else if (responseResult is Result.Error)
                    EventBus.getDefault().post(WebServiceErrorEvent(responseResult))

            }
        }
    }

    suspend fun getMilitaryIncome(token: String, borrowerId: Int, incomeInfoId: Int) {
        //delay(1000)
        viewModelScope.launch(Dispatchers.IO) {
            val responseResult = repo.getMilitaryIncome(
                token = token,
                borrowerId,
                incomeInfoId
            )
            withContext(Dispatchers.Main) {
                if (responseResult is Result.Success)
                    _militaryIncomeData.value = (responseResult.data)
                else if (responseResult is Result.Error && responseResult.exception.message == AppConstant.INTERNET_ERR_MSG)
                    EventBus.getDefault().post(WebServiceErrorEvent(null, true))
                else if (responseResult is Result.Error)
                    EventBus.getDefault().post(WebServiceErrorEvent(responseResult))
            }
        }
    }

    suspend fun getRetirementIncome(token: String, borrowerId: Int, incomeInfoId: Int) {
        //delay(1000)
        viewModelScope.launch(Dispatchers.IO) {
            val responseResult = repo.getRetirementIncome(
                token = token,
                borrowerId,
                incomeInfoId
            )
            withContext(Dispatchers.Main) {
                if (responseResult is Result.Success)
                    _retirementIncomeData.value = (responseResult.data)
                else if (responseResult is Result.Error && responseResult.exception.message == AppConstant.INTERNET_ERR_MSG)
                    EventBus.getDefault().post(WebServiceErrorEvent(null, true))
                else if (responseResult is Result.Error)
                    EventBus.getDefault().post(WebServiceErrorEvent(responseResult))
            }
        }
    }

    suspend fun getRetirementIncomeTypes(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val responseResult = repo.getRetirementIncomeTypes(token = token)
            withContext(Dispatchers.Main) {
                if (responseResult is Result.Success)
                    _retirementIncomeTypes.value = (responseResult.data)
                else if (responseResult is Result.Error && responseResult.exception.message == AppConstant.INTERNET_ERR_MSG)
                    EventBus.getDefault().post(WebServiceErrorEvent(null, true))
                else if (responseResult is Result.Error)
                    EventBus.getDefault().post(WebServiceErrorEvent(responseResult))
            }
        }
    }

    suspend fun getOtherIncome(token: String, incomeInfoId: Int){
        //Log.e("Viewmodel","incomeID: " + incomeInfoId)
        viewModelScope.launch(Dispatchers.IO) {
            val responseResult = repo.getOtherIncome(token = token, incomeInfoId)
            withContext(Dispatchers.Main) {
                if (responseResult is Result.Success)
                    _otherIncomeData.value = (responseResult.data)
                else if (responseResult is Result.Error && responseResult.exception.message == AppConstant.INTERNET_ERR_MSG)
                    EventBus.getDefault().post(WebServiceErrorEvent(null, true))
                else if (responseResult is Result.Error)
                    EventBus.getDefault().post(WebServiceErrorEvent(responseResult))
            }
        }
    }

    suspend fun getOtherIncomeTypes(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val responseResult = repo.getOtherIncomeIncomeTypes(token = token)
            withContext(Dispatchers.Main) {
                if (responseResult is Result.Success)
                    _otherIncomeTypes.value = (responseResult.data)
                else if (responseResult is Result.Error && responseResult.exception.message == AppConstant.INTERNET_ERR_MSG)
                    EventBus.getDefault().post(WebServiceErrorEvent(null, true))
                else if (responseResult is Result.Error)
                    EventBus.getDefault().post(WebServiceErrorEvent(responseResult))
            }
        }
    }

    suspend fun getBusinessTypes(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val responseResult = repo.getBusinessTypes(token = token)
            withContext(Dispatchers.Main) {
                if (responseResult is Result.Success)
                    _businessTypes.value = (responseResult.data)
                else if (responseResult is Result.Error && responseResult.exception.message == AppConstant.INTERNET_ERR_MSG)
                    EventBus.getDefault().post(WebServiceErrorEvent(null, true))
                else if (responseResult is Result.Error)
                    EventBus.getDefault().post(WebServiceErrorEvent(responseResult))
            }
        }
    }

    suspend fun sendSelfEmploymentData(token: String,selfEmploymentData: SelfEmploymentData) {
        Log.e("ViewModel", "inside-SendData")
        viewModelScope.launch(Dispatchers.IO) {
            val responseResult = repo.sendSelfEmploymentData(token = token, selfEmploymentData)
            withContext(Dispatchers.Main) {
                if (responseResult is Result.Success) {
                    Log.e("viewmodel", "success")
                    EventBus.getDefault().post(SendDataEvent(responseResult.data))
                }
                else if (responseResult is Result.Error && responseResult.exception.message == AppConstant.INTERNET_ERR_MSG)
                    //EventBus.getDefault().post(WebServiceErrorEvent(null, true))
                    EventBus.getDefault().post(SendDataEvent(AddUpdateDataResponse(AppConstant.INTERNET_ERR_CODE, null, AppConstant.INTERNET_ERR_MSG, null)))

                else if (responseResult is Result.Error)
                    EventBus.getDefault().post(SendDataEvent(AddUpdateDataResponse("600", null, "Webservice Error", null)))
                    //EventBus.getDefault().post(WebServiceErrorEvent(responseResult))
            }
        }
    }

    suspend fun sendCurrentEmploymentData(token: String,EmploymentData: AddCurrentEmploymentModel) {
        viewModelScope.launch(Dispatchers.IO) {
            val responseResult = repo.sendCurrentEmploymentData(token = token, EmploymentData)
            withContext(Dispatchers.Main) {
                if (responseResult is Result.Success) {
                    EventBus.getDefault().post(SendDataEvent(responseResult.data))
                } else if (responseResult is Result.Error && responseResult.exception.message == AppConstant.INTERNET_ERR_MSG){
                    //EventBus.getDefault().post(WebServiceErrorEvent(null, true))
                    EventBus.getDefault().post(SendDataEvent(AddUpdateDataResponse(AppConstant.INTERNET_ERR_CODE, null, AppConstant.INTERNET_ERR_MSG, null)))

                } else if (responseResult is Result.Error){
                    //EventBus.getDefault().post(WebServiceErrorEvent(responseResult))
                    EventBus.getDefault().post(SendDataEvent(AddUpdateDataResponse("600", null, "Webservice Error", null)))
                }
            }
        }
    }

    suspend fun sendPrevEmploymentData(token: String,employmentData: PreviousEmploymentData) {
        viewModelScope.launch(Dispatchers.IO) {
            val responseResult = repo.sendPrevEmploymentData(token = token, employmentData)
            withContext(Dispatchers.Main) {
                if (responseResult is Result.Success) {
                    //Log.e("Viewmodel", "${responseResult.data}")
                   // Log.e("Viewmodel", "$responseResult")
                    EventBus.getDefault().post(SendDataEvent(responseResult.data))
                } else if (responseResult is Result.Error && responseResult.exception.message == AppConstant.INTERNET_ERR_MSG)
                    EventBus.getDefault().post(SendDataEvent(AddUpdateDataResponse(AppConstant.INTERNET_ERR_CODE, null, AppConstant.INTERNET_ERR_MSG, null)))
                else if (responseResult is Result.Error)
                    EventBus.getDefault().post(SendDataEvent(AddUpdateDataResponse("600", null, "Webservice Error", null)))

            }
        }
    }

    suspend fun sendBusinessData(token: String,businessData: BusinessData) {
        viewModelScope.launch(Dispatchers.IO) {
            val responseResult = repo.sendBusinessData(token = token, businessData)
            withContext(Dispatchers.Main) {
                if (responseResult is Result.Success) {
                    //Log.e("Viewmodel", "${responseResult.data}")
                    //Log.e("Viewmodel", "$responseResult")
                    EventBus.getDefault().post(SendDataEvent(responseResult.data))
                } else if (responseResult is Result.Error && responseResult.exception.message == AppConstant.INTERNET_ERR_MSG)
                    EventBus.getDefault().post(SendDataEvent(AddUpdateDataResponse(AppConstant.INTERNET_ERR_CODE, null, AppConstant.INTERNET_ERR_MSG, null)))

                else if (responseResult is Result.Error)
                    EventBus.getDefault().post(SendDataEvent(AddUpdateDataResponse("600", null, "Webservice Error", null)))

            }
        }
    }

    suspend fun sendMilitaryIncomeData(token: String,businessData: MilitaryIncomeData) {
        viewModelScope.launch(Dispatchers.IO) {
            val responseResult = repo.sendMilitaryData(token = token, businessData)
            withContext(Dispatchers.Main) {
                if (responseResult is Result.Success) {
                    Log.e("Viewmodel", "${responseResult.data}")
                    Log.e("Viewmodel", "$responseResult")
                    EventBus.getDefault().post(SendDataEvent(responseResult.data))
                } else if (responseResult is Result.Error && responseResult.exception.message == AppConstant.INTERNET_ERR_MSG)
                    EventBus.getDefault().post(SendDataEvent(AddUpdateDataResponse(AppConstant.INTERNET_ERR_CODE, null, AppConstant.INTERNET_ERR_MSG, null)))

                else if (responseResult is Result.Error)
                    EventBus.getDefault().post(SendDataEvent(AddUpdateDataResponse("600", null, "Webservice Error", null)))

            }
        }
    }

    suspend fun sendRetiremnentData(token: String, data: RetirementIncomeData) {
        viewModelScope.launch(Dispatchers.IO) {
            val responseResult = repo.sendRetirementData(token = token, data)
            withContext(Dispatchers.Main) {
                if (responseResult is Result.Success) {
                    //Log.e("Viewmodel", "$responseResult")
                    EventBus.getDefault().post(SendDataEvent(responseResult.data))
                } else if (responseResult is Result.Error && responseResult.exception.message == AppConstant.INTERNET_ERR_MSG)
                    EventBus.getDefault().post(SendDataEvent(AddUpdateDataResponse(AppConstant.INTERNET_ERR_CODE, null, AppConstant.INTERNET_ERR_MSG, null)))
                else if (responseResult is Result.Error)
                    EventBus.getDefault().post(SendDataEvent(AddUpdateDataResponse("600", null, "Webservice Error", null)))
            }
        }
    }

    suspend fun sendOtherIncome(token: String,data: AddOtherIncomeInfo){
        viewModelScope.launch(Dispatchers.IO) {
            val responseResult = repo.sendOtherIncomeData(token = token, data)
            withContext(Dispatchers.Main) {
                if (responseResult is Result.Success) {
                    Log.e("Viewmodel", "${responseResult.data}")
                    Log.e("Viewmodel", "$responseResult")
                    EventBus.getDefault().post(SendDataEvent(responseResult.data))
                } else if (responseResult is Result.Error && responseResult.exception.message == AppConstant.INTERNET_ERR_MSG)
                    EventBus.getDefault().post(SendDataEvent(AddUpdateDataResponse("600", null, "Webservice Error", null)))
                else if (responseResult is Result.Error)
                    //EventBus.getDefault().post(WebServiceErrorEvent(responseResult))
                    EventBus.getDefault().post(SendDataEvent(AddUpdateDataResponse("600", null, "Webservice Error", null)))

            }
        }
    }

    suspend fun deleteIncome(token: String, incomeInfoId: Int, borrowerId:Int, loanApplicationId:Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val responseResult = repo.deleteIncomeSource(token = token,incomeInfoId, borrowerId = borrowerId, loanApplicationId = loanApplicationId)
            withContext(Dispatchers.Main) {
                if (responseResult is Result.Success)
                    _addUpdateIncomeResponse.value = (responseResult.data)
                else if (responseResult is Result.Error && responseResult.exception.message == AppConstant.INTERNET_ERR_MSG)
                    EventBus.getDefault().post(WebServiceErrorEvent(null, true))
                else if (responseResult is Result.Error)
                    EventBus.getDefault().post(WebServiceErrorEvent(responseResult))
            }
        }
    }

}