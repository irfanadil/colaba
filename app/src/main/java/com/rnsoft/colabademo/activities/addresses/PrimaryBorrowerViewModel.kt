package com.rnsoft.colabademo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rnsoft.colabademo.activities.model.StatesModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Anita Kiran on 10/29/2021.
 */

@HiltViewModel
class PrimaryBorrowerViewModel @Inject constructor(
    private val repo : PrimaryBorrowerRepo,
    private val commonRepo : CommonRepo
) : ViewModel() {

    private val _borrowerDetail : MutableLiveData<PrimaryBorrowerResponse?> = MutableLiveData()
    val borrowerDetail : LiveData<PrimaryBorrowerResponse?> get() = _borrowerDetail

    private val _countries : MutableLiveData<ArrayList<CountriesModel>> =   MutableLiveData()
    val countries: LiveData<ArrayList<CountriesModel>> get() = _countries

    private val _counties : MutableLiveData<ArrayList<CountiesModel>> =   MutableLiveData()
    val counties: LiveData<ArrayList<CountiesModel>> get() = _counties

    private val _states : MutableLiveData<ArrayList<StatesModel>> =   MutableLiveData()
    val states: LiveData<ArrayList<StatesModel>> get() = _states

    private val _housingStatus: MutableLiveData<ArrayList<OptionsResponse>> = MutableLiveData()
    val housingStatus: LiveData<ArrayList<OptionsResponse>> get() = _housingStatus

    private val _relationships: MutableLiveData<ArrayList<DropDownResponse>?> = MutableLiveData()
    val relationships: LiveData<ArrayList<DropDownResponse>?> get() = _relationships

    private val _addUpdateDeleteResponse: MutableLiveData<AddUpdateDataResponse> = MutableLiveData()
    val addUpdateDeleteResponse: LiveData<AddUpdateDataResponse> get() = _addUpdateDeleteResponse

    private val _updatedAddress : MutableLiveData<PreviousAddresses?> = MutableLiveData()
    val updatedAddress : LiveData<PreviousAddresses?> get() = _updatedAddress

    private val _mailingAddress : MutableLiveData<AddressModel?> = MutableLiveData()
    val mailingAddress : LiveData<AddressModel?> get() = _mailingAddress


    fun savePreviousAddress(address: PreviousAddresses) {
        _updatedAddress.postValue(address)
    }

    fun saveMailingAddress(address: AddressModel) {
        _mailingAddress.postValue(address)
    }

    fun emptyMailingAddress(){
        _mailingAddress.value = null
        _mailingAddress.postValue( null)
    }

    fun emptyAddress(){
        _updatedAddress.value = null
        _updatedAddress.postValue( null)
    }

    suspend fun deletePreviousAddress(token: String, loanApplicationId: Int,id: Int){
        //Log.e("delete Address","loan id "+ loanApplicationId+"Id " + id)
        viewModelScope.launch(Dispatchers.IO) {
            val responseResult = repo.deletePreviousAddress(token = token,loanApplicationId,id)
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

    suspend fun getBasicBorrowerDetail(token : String, loanApplicationId : Int, borrowerId : Int) {
        viewModelScope.launch(Dispatchers.IO){
            val responseResult = repo.getPrimaryBorrowerDetails(token = token, loanApplicationId = loanApplicationId,borrowerId = borrowerId)
            withContext(Dispatchers.Main) {
                if (responseResult is Result.Success) {
                    _borrowerDetail.value = (responseResult.data)
                } else if (responseResult is Result.Error && responseResult.exception.message == AppConstant.INTERNET_ERR_MSG)
                    EventBus.getDefault().post(WebServiceErrorEvent(null, true))
                else if (responseResult is Result.Error)
                    EventBus.getDefault().post(WebServiceErrorEvent(responseResult))
            }
        }
    }

    fun refreshBorrowerInfo(){
        _borrowerDetail.value = null
        _borrowerDetail.postValue(null)
    }

    suspend fun addUpdateBorrowerInfo(token: String,data: PrimaryBorrowerData) {
        //Log.e("ViewModel", "inside-SendData")
        viewModelScope.launch(Dispatchers.IO) {
            val responseResult = repo.sendBorrowerInfo(token = token, data)
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

    suspend fun getHousingStatus(token: String){
        viewModelScope.launch(Dispatchers.IO) {
            val responseResult = repo.getHousingStatus(token = token)
            withContext(Dispatchers.Main) {
                if (responseResult is Result.Success)
                    _housingStatus.value = (responseResult.data)
                /*else if (responseResult is Result.Error && responseResult.exception.message == AppConstant.INTERNET_ERR_MSG)
                        EventBus.getDefault().post(WebServiceErrorEvent(null, true))
                     else if (responseResult is Result.Error)
                        EventBus.getDefault().post(WebServiceErrorEvent(responseResult))
                    */
            }
        }
    }

    suspend fun getRelationshipTypes(token:String) {
        _relationships.value = null
        _relationships.postValue(null)

        viewModelScope.launch(Dispatchers.IO) {
            val response = repo.getRelationshipTypes(token = token )
            withContext(Dispatchers.Main) {
                if (response is Result.Success)
                    _relationships.value = (response.data)
                else if (response is Result.Error && response.exception.message == AppConstant.INTERNET_ERR_MSG)
                    EventBus.getDefault().post(WebServiceErrorEvent(null, true))
                else if (response is Result.Error)
                    EventBus.getDefault().post(WebServiceErrorEvent(null, true))
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

}