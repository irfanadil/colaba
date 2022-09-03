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
class CommonViewModel @Inject constructor(private val commonRepo: CommonRepo) : ViewModel() {


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

}