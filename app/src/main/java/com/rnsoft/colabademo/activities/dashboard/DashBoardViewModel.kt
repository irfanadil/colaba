package com.rnsoft.colabademo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@HiltViewModel
class DashBoardViewModel @Inject constructor(private val dashBoardRepo : DashBoardRepo) :
    ViewModel() {

    private val _notificationItemList : MutableLiveData<ArrayList<NotificationItem>> =   MutableLiveData()
    val notificationItemList: LiveData<ArrayList<NotificationItem>> get() = _notificationItemList

    private val _notificationCount: MutableLiveData<Int> =  MutableLiveData()
    val notificationCount:LiveData<Int> = _notificationCount


    fun getNotificationCountT(token:String) {
         viewModelScope.launch {
             val result = dashBoardRepo.getNotificationCount(token)
             if (result is Result.Success)
                 _notificationCount.value = result.data.count
             else if (result is Result.Error && result.exception.message == AppConstant.INTERNET_ERR_MSG)
                 _notificationCount.value = -1
             else
                 _notificationCount.value = -100
         }
     }

    suspend fun getNotificationListing(token:String, pageSize:Int, lastId:Int, mediumId:Int) {
            viewModelScope.launch {
                val responseResult = dashBoardRepo.getNotificationListing(
                    token = token,
                    pageSize = pageSize,
                    lastId = lastId,
                    mediumId = mediumId
                )
                if (responseResult is Result.Success)
                    _notificationItemList.value = (responseResult.data)
                else if(responseResult is Result.Error && responseResult.exception.message == AppConstant.INTERNET_ERR_MSG)
                    EventBus.getDefault().post(WebServiceErrorEvent(null, true))
                else if(responseResult is Result.Error)
                    EventBus.getDefault().post(WebServiceErrorEvent(responseResult))

        }
    }

    fun getFurtherNotificationList(token:String, pageSize:Int, lastId:Int, mediumId:Int) {
            viewModelScope.launch {
                val responseResult = dashBoardRepo.getNotificationListing(
                    token = token, pageSize = pageSize,
                    lastId = lastId, mediumId = mediumId
                )
                if (responseResult is Result.Success) {
                    _notificationItemList.value = (responseResult.data)
                }
                else if(responseResult is Result.Error && responseResult.exception.message == AppConstant.INTERNET_ERR_MSG)
                    EventBus.getDefault().post(WebServiceErrorEvent(null, true))
                else if(responseResult is Result.Error)
                    EventBus.getDefault().post(WebServiceErrorEvent(responseResult))
            }
    }

    fun seenNotifications(token:String , ids:ArrayList<Int>) {
        viewModelScope.launch {
            val result = dashBoardRepo.seenNotifications(token, ids)
            if (result is Result.Success) {

            }
            else if (result is Result.Error && result.exception.message == AppConstant.INTERNET_ERR_MSG){
                EventBus.getDefault().post(WebServiceErrorEvent(null, true))
            }
            //else if(result is Result.Error)
                //EventBus.getDefault().post(WebServiceErrorEvent(result))
        }
    }

    fun readNotifications(token:String , ids:ArrayList<Int>) {
        viewModelScope.launch {
            val result = dashBoardRepo.readNotifications(token, ids)
            if (result is Result.Success) {
                Log.e("read-notify-", result.toString())
            }
            else if(result is Result.Error && result.exception.message == AppConstant.INTERNET_ERR_MSG)
                EventBus.getDefault().post(WebServiceErrorEvent(null, true))
            //else if(result is Result.Error)
                //EventBus.getDefault().post(WebServiceErrorEvent(result))

        }
    }

    fun deleteNotifications(token:String , ids:ArrayList<Int>) {
        viewModelScope.launch {
            val result = dashBoardRepo.deleteNotifications(token, ids)
            if (result is Result.Success) {
                Log.e("del-notify-", result.toString())
            }
            else if(result is Result.Error && result.exception.message == AppConstant.INTERNET_ERR_MSG)
                EventBus.getDefault().post(WebServiceErrorEvent(null, true))
            //else if(result is Result.Error)
               // EventBus.getDefault().post(WebServiceErrorEvent(result))
        }
    }

}