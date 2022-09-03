package com.rnsoft.colabademo

import android.util.Log
import com.rnsoft.colabademo.*
import java.io.IOException
import javax.inject.Inject

class SearchDataSource @Inject constructor(private val serverApi: ServerApi){

    suspend fun searchByText(token:String , pageNumber:Int , pageSize:Int, searchTerm:String)
    :Result<ArrayList<SearchItem>>
    {
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.searchByText( pageNumber = pageNumber, pageSize = pageSize, searchTerm = searchTerm)
            Log.e("response-", response.toString())
            Result.Success(response)
        } catch (e: Throwable) {
            if(e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error logging in", e))
        }
    }
}