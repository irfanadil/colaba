package com.rnsoft.colabademo

import android.util.Log
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

/**
 * Created by Anita Kiran on 11/1/2021.
 */
class IncomeDataSource @Inject constructor(private val serverApi: ServerApi) {

    suspend fun getEmploymentDetails(
        token: String, loanApplicationId: Int, borrowerId: Int, incomeInfoId: Int): Result<EmploymentDetailResponse> {
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.getEmploymentDetail(newToken,loanApplicationId,borrowerId,incomeInfoId)
            Timber.e("Employment Response-- $response")
            Result.Success(response)
        } catch (e: Throwable) {
            if (e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error notification -", e))
        }
    }

    suspend fun getSelfEmploymentDetails(
        token: String, borrowerId: Int, incomeInfoId: Int): Result<SelfEmploymentResponse> {
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.getSelfEmploymentContractor(newToken,borrowerId,incomeInfoId)
            Timber.e("Self-Employment-Response-- $response")
            Result.Success(response)
        } catch (e: Throwable) {
            if (e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error notification -", e))
        }
    }

    suspend fun getBusinessIncome(
        token: String, borrowerId: Int, incomeInfoId: Int): Result<BusinessIncomeResponse> {
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.getBusinessIncome(newToken,borrowerId,incomeInfoId)
            Timber.e("business-income-Response-- $response")
            Result.Success(response)
        } catch (e: Throwable) {
            if (e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error notification -", e))
        }
    }

    suspend fun getMilitaryIncome(
        token: String, borrowerId: Int, incomeInfoId: Int): Result<MilitaryIncomeResponse> {
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.getMilitaryIncome(newToken,borrowerId,incomeInfoId)
            //Timber.e("military-income-Response-- $response")
            Result.Success(response)
        } catch (e: Throwable) {
            if (e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error notification -", e))
        }
    }

    suspend fun getRetirementIncome(
        token: String, borrowerId: Int, incomeInfoId: Int): Result<RetirementIncomeResponse> {
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.getRetirementIncome(newToken,borrowerId,incomeInfoId)
            //Timber.e("retirement-income-Response-- $response")
            Result.Success(response)
        } catch (e: Throwable) {
            if (e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error notification -", e))
        }
    }

    suspend fun getRetirementIncomeTypes(
        token: String): Result<ArrayList<DropDownResponse>> {
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.getRetirementIncomeTypes(newToken)
            //Timber.e("retirement-income-types-- $response")
            Result.Success(response)
        } catch (e: Throwable) {
            if (e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error notification -", e))
        }
    }

    suspend fun getOtherIncome(
        token: String, incomeInfoId: Int): Result<OtherIncomeResponse> {
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.getOtherIncomeInfo(newToken,incomeInfoId)
            Timber.e("other-income-Response-- $response")
            Result.Success(response)
        } catch (e: Throwable) {
            if (e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error notification -", e))
        }
    }



    suspend fun getOtherIncomeTypes(
        token: String): Result<ArrayList<DropDownResponse>> {
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.getOtherIncomeTypes(newToken)
            //Timber.e("other-income-Response-- $response")
            Result.Success(response)
        } catch (e: Throwable) {
            if (e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error notification -", e))
        }
    }

    suspend fun getBusinessTypes(
        token: String): Result<ArrayList<DropDownResponse>> {
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.getAllBusinessTypes(newToken)
            //Timber.e("other-income-Response-- $response")
            Result.Success(response)
        } catch (e: Throwable) {
            if (e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error notification -", e))
        }
    }

    suspend fun sendSelfEmploymentContractorData(token: String, data: SelfEmploymentData): Result<AddUpdateDataResponse> {
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.addOrUpdateSelfBusiness(newToken,data)
            Log.e("incomeDatasource","$response")
            Result.Success(response)
        } catch (e: Throwable){
            if(e is HttpException){
                //Log.e("incomeDatasource1",e.localizedMessage)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            }
            else {
                //Log.e("incomeDatasource2",e.localizedMessage)
                Result.Error(IOException("Error logging in", e))
            }
        }
    }

    suspend fun sendCurrentEmploymentData(token: String, data: AddCurrentEmploymentModel): Result<AddUpdateDataResponse> {
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.addCurrentEmployment(newToken,data)
            //Log.e("add-CurrentEmployment-respone","$response")
            Result.Success(response)
        } catch (e: Throwable){
            if(e is HttpException){
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            }
            else {
               // Log.e("add-CurrentEmployment-Error",e.localizedMessage)
                Result.Error(IOException("Error logging in", e))
            }
        }
    }

    suspend fun sendPrevEmploymentData(token: String, data: PreviousEmploymentData): Result<AddUpdateDataResponse> {
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.addPreviousEmployment(newToken,data)
           // Log.e("add-PrevEmployment-respone","$response")
            Result.Success(response)
        } catch (e: Throwable){
            if(e is HttpException){
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            }
            else {
                //Log.e("add-PrevEmployment-Error",e.localizedMessage)
                Result.Error(IOException("Error logging in", e))
            }
        }
    }

    suspend fun sendBusinessIncomeData(token: String, data: BusinessData): Result<AddUpdateDataResponse> {
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.addUpdateBusiness(newToken,data)
            //Log.e("business-respone","$response")
            Result.Success(response)
        } catch (e: Throwable){
            if(e is HttpException){
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            }
            else {
                //Log.e("add-business-Error",e.localizedMessage)
                Result.Error(IOException("Error logging in", e))
            }
        }
    }

    suspend fun sendMilitaryIncomeData(token: String, data:MilitaryIncomeData): Result<AddUpdateDataResponse> {
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.addUpdateMilitaryIncome(newToken,data)
            Log.e("military income-respone","$response")
            Result.Success(response)
        } catch (e: Throwable){
            if(e is HttpException){
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            }
            else {
                //Log.e("add-business-Error",e.localizedMessage)
                Result.Error(IOException("Error logging in", e))
            }
        }
    }

    suspend fun sendRetirementIncomeData(token: String, data:RetirementIncomeData): Result<AddUpdateDataResponse> {
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.addUpdateRetirement(newToken,data)
            //Log.e("retirement-respone","$response")
            Result.Success(response)
        } catch (e: Throwable){
            if(e is HttpException){
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            }
            else {
                Log.e("add-retirement-Error",e.localizedMessage)
                Result.Error(IOException("Error logging in", e))
            }
        }
    }

    suspend fun sendOtherIncomeData(token: String, data: AddOtherIncomeInfo): Result<AddUpdateDataResponse> {
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.addUpdateOtherIncome(newToken,data)
            Log.e("other-respone","$response")
            Result.Success(response)
        } catch (e: Throwable){
            if(e is HttpException){
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            }
            else {
                Log.e("other-income-Error",e.localizedMessage)
                Result.Error(IOException("Error logging in", e))
            }
        }
    }

    suspend fun deleteIncome(token : String, incomeInfoId:Int, borrowerId:Int, loanApplicationId:Int): Result<GenericAddUpdateAssetResponse> {
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.deleteIncome(newToken, incomeInfoId, borrowerId,loanApplicationId)
            Timber.e("deleteIncomeRESPONSE = $response")
            Result.Success(response)
        } catch (e: Throwable) {
            if (e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error notification -", e))
        }
    }


}