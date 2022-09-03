package com.rnsoft.colabademo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rnsoft.colabademo.*
import com.rnsoft.colabademo.activities.model.StatesModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Anita Kiran on 10/15/2021.
 */
@HiltViewModel
class RealEstateViewModel @Inject constructor(private val repo: RealEstateRepo, private val commonRepo: CommonRepo) : ViewModel() {

    private val _realEstateDetails : MutableLiveData<RealEstateResponse> =   MutableLiveData()
    val realEstateDetails: LiveData<RealEstateResponse> get() = _realEstateDetails

    private val _firstMortgageDetails : MutableLiveData<RealEstateFirstMortgageModel> =   MutableLiveData()
    val firstMortgageDetails: LiveData<RealEstateFirstMortgageModel> get() = _firstMortgageDetails

    private val _secondMortgageDetails : MutableLiveData<RealEstateSecondMortgageModel> =   MutableLiveData()
    val secondMortgageDetails: LiveData<RealEstateSecondMortgageModel> get() = _secondMortgageDetails

    private val _propertyType : MutableLiveData<ArrayList<DropDownResponse>> =   MutableLiveData()
    val propertyType: LiveData<ArrayList<DropDownResponse>> get() = _propertyType

    private val _propertyStatus : MutableLiveData<ArrayList<DropDownResponse>> =   MutableLiveData()
    val propertyStatus: LiveData<ArrayList<DropDownResponse>> get() = _propertyStatus

    private val _occupancyType : MutableLiveData<ArrayList<DropDownResponse>> =   MutableLiveData()
    val occupancyType: LiveData<ArrayList<DropDownResponse>> get() = _occupancyType

    private val _countries : MutableLiveData<ArrayList<CountriesModel>> =   MutableLiveData()
    val countries: LiveData<ArrayList<CountriesModel>> get() = _countries

    private val _counties : MutableLiveData<ArrayList<CountiesModel>> =   MutableLiveData()
    val counties: LiveData<ArrayList<CountiesModel>> get() = _counties

    private val _states : MutableLiveData<ArrayList<StatesModel>> =   MutableLiveData()
    val states: LiveData<ArrayList<StatesModel>> get() = _states

    private val _addUpdateDeleteResponse: MutableLiveData<AddUpdateDataResponse> = MutableLiveData()
    val addUpdateDeleteResponse: LiveData<AddUpdateDataResponse> get() = _addUpdateDeleteResponse


    suspend fun sendRealEstate(token: String,data: AddRealEstateResponse) {
        //Log.e("ViewModel", "inside-SendData")
        viewModelScope.launch(Dispatchers.IO) {
            val responseResult = repo.sendRealEstateDetails(token = token, data)
            withContext(Dispatchers.Main) {
                if (responseResult is Result.Success) {
                    EventBus.getDefault().post(SendDataEvent(responseResult.data))
                } else if (responseResult is Result.Error && responseResult.exception.message == AppConstant.INTERNET_ERR_MSG)
                    EventBus.getDefault().post(SendDataEvent(AddUpdateDataResponse(AppConstant.INTERNET_ERR_CODE, null, AppConstant.INTERNET_ERR_MSG, null)))

                else if (responseResult is Result.Error)
                    EventBus.getDefault().post(SendDataEvent(AddUpdateDataResponse("600", null, "Webservice Error", null)))
            }
        }
    }

    suspend fun getRealEstateDetails(token:String, loanApplicationId:Int,borrowerPropertyId:Int) {
        //Timber.e("loanAppliactionId " + loanApplicationId + "propertyId : " + borrowerPropertyId + "Token: "  + token)
        viewModelScope.launch(Dispatchers.IO) {
            val responseResult = repo.getRealEstateDetails(token = token, loanApplicationId = loanApplicationId, borrowerPropertyId = borrowerPropertyId)
            withContext(Dispatchers.Main) {
                if (responseResult is Result.Success){
                    _realEstateDetails.value = (responseResult.data)
                }
                else if (responseResult is Result.Error && responseResult.exception.message == AppConstant.INTERNET_ERR_MSG) {
                    EventBus.getDefault().post(WebServiceErrorEvent(null, true))
                }
                else if (responseResult is Result.Error){
                    EventBus.getDefault().post(WebServiceErrorEvent(responseResult))
               }
            }
        }
    }

    suspend fun getFirstMortgageDetails(token:String, loanApplicationId:Int,borrowerPropertyId:Int) {
        //Log.e("viewmodel","id" + loanApplicationId + "propertyId" + borrowerPropertyId + "  " + token)
        viewModelScope.launch(Dispatchers.IO) {
            val responseResult = repo.getRealEstateFirstMortgageDetails(token = token, loanApplicationId = loanApplicationId, borrowerPropertyId = borrowerPropertyId)
            withContext(Dispatchers.Main) {
                if (responseResult is Result.Success){
                    _firstMortgageDetails.value = (responseResult.data)
                }
                else if (responseResult is Result.Error && responseResult.exception.message == AppConstant.INTERNET_ERR_MSG) {
                    EventBus.getDefault().post(WebServiceErrorEvent(null, true))

                }
                else if (responseResult is Result.Error){

                    EventBus.getDefault().post(WebServiceErrorEvent(responseResult))
                }
            }
        }
    }

    suspend fun getSecondMortgageDetails(token:String, loanApplicationId:Int,borrowerPropertyId:Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val responseResult = repo.getRealEstateSecondMortgageDetails(token = token, loanApplicationId = loanApplicationId, borrowerPropertyId = borrowerPropertyId)
            withContext(Dispatchers.Main) {
                if (responseResult is Result.Success){
                    _secondMortgageDetails.value = (responseResult.data)
                }
                else if (responseResult is Result.Error && responseResult.exception.message == AppConstant.INTERNET_ERR_MSG) {
                    EventBus.getDefault().post(WebServiceErrorEvent(null, true))
                }
                else if (responseResult is Result.Error){
                    //Timber.e(WebServiceErrorEvent(responseResult))
                    EventBus.getDefault().post(WebServiceErrorEvent(responseResult))
                }
            }
        }
    }

    suspend fun getPropertyTypes(token:String) {
        viewModelScope.launch() {
            val responseResult = commonRepo.getPropertyType(token = token)
            withContext(Dispatchers.Main) {
                if (responseResult is Result.Success)
                    _propertyType.value = (responseResult.data)
                else if (responseResult is Result.Error && responseResult.exception.message == AppConstant.INTERNET_ERR_MSG)
                    EventBus.getDefault().post(WebServiceErrorEvent(null, true))
                else if (responseResult is Result.Error)
                    EventBus.getDefault().post(WebServiceErrorEvent(responseResult))
            }
        }
    }

    suspend fun getPropertyStatus(token:String) {
        viewModelScope.launch() {
            val responseResult = repo.getPropertyStatus(token = token)
            withContext(Dispatchers.Main) {
                if (responseResult is Result.Success)
                    _propertyStatus.value = (responseResult.data)
                else if (responseResult is Result.Error && responseResult.exception.message == AppConstant.INTERNET_ERR_MSG)
                    EventBus.getDefault().post(WebServiceErrorEvent(null, true))
                else if (responseResult is Result.Error)
                    EventBus.getDefault().post(WebServiceErrorEvent(responseResult))
            }
        }
    }

    suspend fun getOccupancyType(token:String) {
        viewModelScope.launch() {
            val responseResult = commonRepo.getOccupancyType(token = token )
            withContext(Dispatchers.Main) {
                if (responseResult is Result.Success)
                    _occupancyType.value = (responseResult.data)
                else if (responseResult is Result.Error && responseResult.exception.message == AppConstant.INTERNET_ERR_MSG)
                    EventBus.getDefault().post(WebServiceErrorEvent(null, true))
                else if (responseResult is Result.Error)
                    EventBus.getDefault().post(WebServiceErrorEvent(responseResult))
            }
        }
    }

    suspend fun getStates(token:String) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = commonRepo.getStates(token = token )
            withContext(Dispatchers.Main) {
                if (response is Result.Success)
                    _states.value = (response.data)
                else if (response is Result.Error && response.exception.message == AppConstant.INTERNET_ERR_MSG)
                    EventBus.getDefault().post(WebServiceErrorEvent(null, true))
                else if (response is Result.Error)
                    EventBus.getDefault().post(WebServiceErrorEvent(response))
            }
        }
    }

    suspend fun getCounty(token:String) {
        viewModelScope.launch(Dispatchers.IO) {
            val responseResult = commonRepo.getCounties(token = token)
            withContext(Dispatchers.Main) {
                if (responseResult is Result.Success)
                    _counties.value = (responseResult.data)
                else if (responseResult is Result.Error && responseResult.exception.message == AppConstant.INTERNET_ERR_MSG)
                    EventBus.getDefault().post(WebServiceErrorEvent(null, true))
                else if (responseResult is Result.Error)
                    EventBus.getDefault().post(WebServiceErrorEvent(responseResult))
            }
        }
    }

    suspend fun getCountries(token:String) {
        viewModelScope.launch(Dispatchers.IO) {
            val responseResult = commonRepo.getCountries(token = token )
            withContext(Dispatchers.Main) {
                if (responseResult is Result.Success)
                    _countries.value = (responseResult.data)
                else if (responseResult is Result.Error && responseResult.exception.message == AppConstant.INTERNET_ERR_MSG)
                    EventBus.getDefault().post(WebServiceErrorEvent(null, true))
                else if (responseResult is Result.Error)
                    EventBus.getDefault().post(WebServiceErrorEvent(responseResult))
            }
        }
    }

    suspend fun deleteRealEstate(token: String, borrowerPropertyId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val responseResult = repo.deleteRealEstate(token = token,borrowerPropertyId)
            withContext(Dispatchers.Main) {
                if (responseResult is Result.Success)
                    _addUpdateDeleteResponse.value = (responseResult.data)
                else if (responseResult is Result.Error && responseResult.exception.message == AppConstant.INTERNET_ERR_MSG)
                    EventBus.getDefault().post(WebServiceErrorEvent(null, true))
                else if (responseResult is Result.Error)
                    EventBus.getDefault().post(WebServiceErrorEvent(responseResult))
            }
        }
    }

}