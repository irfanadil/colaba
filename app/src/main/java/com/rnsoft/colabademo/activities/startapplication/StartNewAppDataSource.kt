package com.rnsoft.colabademo

import android.util.Log
import java.io.IOException
import javax.inject.Inject

class StartNewAppDataSource  @Inject constructor(private val serverApi: ServerApi){

    suspend fun searchByBorrowerContact(token:String, searchKeyword:String):Result<SearchResultResponse>{
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.searchByBorrowerContact( keyword = searchKeyword)
            Log.e("searchByBorrowerContact-", response.toString())
            Result.Success(response)
        } catch (e: Throwable) {
            if(e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error searchByBorrowerContact -", e))
        }
    }

    suspend fun lookUpBorrowerContact(token:String, borrowerEmail:String, borrowerPhone:String?=null):Result<LookUpBorrowerContactResponse>{
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.lookUpBorrowerContact( email = borrowerEmail, phone = borrowerPhone)
            //Log.e("lookUpBorrowerContact-", response.toString())
            Result.Success(response)
        } catch (e: Throwable) {
            if(e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error searchByBorrowerContact -", e))
        }
    }

    suspend fun createApplication(token:String, createNewApplicationParams: CreateNewApplicationParams):Result<CreateNewApplicationResponse>{
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.createApplication( createNewApplicationParams = createNewApplicationParams)
            Log.e("createNewApplicationParams-", response.toString())
            Result.Success(response)
        } catch (e: Throwable) {
            if(e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error searchByBorrowerContact -", e))
        }
    }


    suspend fun getMcusByRoleId(token:String, filterLoanOfficer:Boolean):Result<LoanOfficerApiResponse>{
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.getMcusByRoleId( filterLoanOfficer = filterLoanOfficer)
            Log.e("getMcusByRoleId-", response.toString())
            Result.Success(response)
        } catch (e: Throwable) {
            if(e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error searchByBorrowerContact -", e))
        }
    }





}