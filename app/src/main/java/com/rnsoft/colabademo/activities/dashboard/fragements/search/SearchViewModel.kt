package com.rnsoft.colabademo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private val searchRepo: SearchRepo)  : ViewModel() {

    private val _searchArrayList = MutableLiveData<ArrayList<SearchItem>>()
    val searchArrayList: LiveData<ArrayList<SearchItem>> get() = _searchArrayList
    init {
        _searchArrayList.value = ArrayList()
    }

    private var localSearchList:ArrayList<SearchItem> = ArrayList()

    fun getSearchResult(token:String, pageNumber:Int, pageSize:Int, searchTerm:String)
    {
        viewModelScope.launch {
            delay(1000)
            val result = searchRepo.getSearchResult(token = token , pageNumber = pageNumber, pageSize = pageSize, searchTerm = searchTerm)

            if (result is Result.Success) {
                localSearchList = (result.data)
                _searchArrayList.value = localSearchList
            }
            else if(result is Result.Error && result.exception.message == AppConstant.INTERNET_ERR_MSG)
                EventBus.getDefault().post(WebServiceErrorEvent(null, true))
            else if(result is Result.Error)
                EventBus.getDefault().post(WebServiceErrorEvent(result))
        }
    }

    fun resetSearchData(){
        localSearchList.clear()
    }

}