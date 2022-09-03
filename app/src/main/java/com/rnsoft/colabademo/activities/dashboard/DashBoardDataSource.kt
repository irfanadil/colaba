package com.rnsoft.colabademo

import android.util.Log
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

class DashBoardDataSource  @Inject constructor(private val serverApi: ServerApi){

    suspend fun logoutUser(token: String): Result<LogoutResponse> {
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.logoutUser()
            Log.e("LogoutResponse-", response.toString())
            Result.Success(response)
        } catch (e: Throwable) {
            if(e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error notification -", e))
        }
    }

    suspend fun getNotificationListing(token:String, pageSize:Int, lastId:Int, mediumId:Int):Result<ArrayList<NotificationItem>>{
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.getNotificationListing(
                pageSize = pageSize, lastId = lastId, mediumId = mediumId)
            Log.e("NotificationListItems- ", response.toString())
            Result.Success(response)
        } catch (e: Throwable) {
            if(e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error notification -", e))
        }
    }

    suspend fun getNotificationCount(token:String):Result<TotalNotificationCount>{
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.getNotificationCount()
            Log.e("NotificationCount-", response.toString())
            Result.Success(response)
        } catch (e: Throwable) {
            if(e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error notification -", e))
        }
    }

    suspend fun readNotifications(token:String , ids:ArrayList<Int>):Result<Any>{
        return try {
            val newToken = "Bearer $token"
            val putParams = PutParameters(ids)
            val response = serverApi.readNotifications( putParams)
            Log.e("read-Notifications-", response.toString())
            if(response.isSuccessful)
                Result.Success(response)
            else
                Result.Error(IOException("unknown webservice error"))
        } catch (e: Throwable) {
            if(e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException(e.message))
        }
    }

    suspend fun seenNotifications(token:String,ids:ArrayList<Int>):Result<Any>{
        return try {
            val newToken = "Bearer $token"
            val putParams = PutParameters(ids)
            val response = serverApi.seenNotifications( putParams)
            if (response != null){
                val responseString = response as String
                Log.e("seen-Notifications-", responseString.toString())
                val webResponse = response as Response<Any>

                if (webResponse.isSuccessful)
                    Result.Success(webResponse)
                else {
                    Log.e("NOTHING", "Bingo")
                    Result.Error(IOException("unknown webservice error"))
                }
            }
            else
                Result.Error(IOException("unknown webservice error"))
        } catch (e: Throwable) {
            if(e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error notification -", e))
        }
    }

    suspend fun deleteNotifications(token:String,ids:ArrayList<Int>):Result<Any>{
        return try {
            val newToken = "Bearer $token"
            val putParams = PutParameters(ids)
            val response = serverApi.deleteNotifications( putParams)
            Log.e("delete-Notifications-", response.toString())
            if(response.isSuccessful)
                Result.Success(response)
            else
                Result.Error(IOException("unknown webservice error"))
        } catch (e: Throwable) {
            if(e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error notification -", e))
        }
    }

}