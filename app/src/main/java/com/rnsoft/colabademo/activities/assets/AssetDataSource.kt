package com.rnsoft.colabademo

import android.util.Log
import com.rnsoft.AssetTypesByCategory
import com.rnsoft.colabademo.activities.assets.fragment.model.*
import retrofit2.http.Query
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

/**
 * Created by Anita Kiran on 11/1/2021.
 */

class AssetDataSource @Inject constructor(private val serverApi: ServerApi) {


    suspend fun fetchAssetTypesByCategoryItemList(token: String , categoryId:Int, loanPurposeId:Int) : Result<ArrayList<GetAssetTypesByCategoryItem>> {
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.fetchAssetTypesByCategoryItemList( categoryId = categoryId, loanPurposeId = loanPurposeId)
            //Timber.e("GetAssetTypesByCategoryItem:  - $response")
            Result.Success(response)
        } catch (e: Throwable) {
            if (e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error notification -", e.cause))
        }
    }

    suspend fun getProceedsFromLoan(token: String , loanApplicationId : Int, borrowerId : Int, assetTypeID:Int, borrowerAssetId : Int) : Result<ProceedFromLoanModel> {
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.getProceedsFromLoan(
                loanApplicationId = loanApplicationId, borrowerId = borrowerId,
                AssetTypeId=assetTypeID, borrowerAssetId = borrowerAssetId )
            Timber.e("getProceedsFromLoan:  - $response")
            Result.Success(response)
        } catch (e: Throwable) {
            if (e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error notification -", e.cause))
        }
    }

    suspend fun getProceedsFromNonRealEstateDetail(token: String , loanApplicationId : Int, borrowerId : Int, assetTypeID:Int, borrowerAssetId : Int) : Result<ProceedFromLoanModel> {
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.getFromLoanNonRealStateDetails(
                loanApplicationId = loanApplicationId, borrowerId = borrowerId,
                AssetTypeId=assetTypeID, borrowerAssetId = borrowerAssetId )
            Timber.e("getProceedsFromLoan:  - $response")
            Result.Success(response)
        } catch (e: Throwable) {
            if (e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error notification -", e.cause))
        }
    }


    suspend fun getProceedsFromRealEstateDetail(token: String , loanApplicationId : Int, borrowerId : Int, assetTypeID:Int, borrowerAssetId : Int) : Result<ProceedFromLoanModel> {
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.getFromLoanRealStateDetails(
                loanApplicationId = loanApplicationId, borrowerId = borrowerId,
                AssetTypeId=assetTypeID, borrowerAssetId = borrowerAssetId )
            Timber.e("getProceedsFromLoan:  - $response")
            Result.Success(response)
        } catch (e: Throwable) {
            if (e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error notification -", e.cause))
        }
    }


    suspend fun getBankAccountDetails(token : String, loanApplicationId : Int, borrowerId : Int, borrowerAssetId : Int): Result<BankAccountResponse> {
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.getBankAccountDetails(
                newToken,
                loanApplicationId,
                borrowerId,
                borrowerAssetId
            )
            Timber.e(response.toString())
            Result.Success(response)
        } catch (e: Throwable) {
            if (e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error notification -", e))
        }
    }

    suspend fun addUpdateBankDetails(token : String, addUpdateParams: BankAddUpdateParams): Result<GenericAddUpdateAssetResponse> {
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.addUpdateBankDetails(addUpdateParams)
            Timber.e(response.toString())
            Result.Success(response)
        } catch (e: Throwable) {
            if (e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error notification -", e))
        }
    }

    suspend fun addUpdateOtherAsset(token : String, otherAssetAddUpdateParams: OtherAssetAddUpdateParams): Result<GenericAddUpdateAssetResponse> {
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.addUpdateOtherAsset( otherAssetAddUpdateParams)
            Timber.e(response.toString())
            Result.Success(response)
        } catch (e: Throwable) {
            if (e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error notification -", e))
        }
    }

    suspend fun addUpdateGift(token : String, giftAddUpdateParams: GiftAddUpdateParams ): Result<GenericAddUpdateAssetResponse> {
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.addUpdateGift( giftAddUpdateParams)
            Timber.e("addUpdate = $response")
            Result.Success(response)
        } catch (e: Throwable) {
            if (e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error notification -", e))
        }
    }


    suspend fun addUpdateStockBonds(stocksBondsAddUpdateParams:StocksBondsAddUpdateParams): Result<GenericAddUpdateAssetResponse> {
        return try {
            val response = serverApi.addUpdateStockBonds(stocksBondsAddUpdateParams)
            Timber.e(response.toString())
            Result.Success(response)
        } catch (e: Throwable) {
            if (e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error notification -", e))
        }
    }






    suspend fun addUpdateProceedFromLoan(token : String, addUpdateProceedLoanParams: AddUpdateProceedLoanParams): Result<GenericAddUpdateAssetResponse> {
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.addUpdateProceedFromLoan( addUpdateProceedLoanParams)
            Timber.e(response.toString())
            Result.Success(response)
        } catch (e: Throwable) {
            if (e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error notification -", e))
        }
    }

    suspend fun addUpdateProceedFromLoanOther(token : String, addUpdateProceedFromLoanOtherParams: AddUpdateProceedFromLoanOtherParams): Result<GenericAddUpdateAssetResponse> {
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.addUpdateProceedFromLoanOther(
                addUpdateProceedFromLoanOtherParams
            )
            Timber.e(response.toString())
            Result.Success(response)
        } catch (e: Throwable) {
            if (e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error notification -", e))
        }
    }


    suspend fun addUpdateAssetsRealStateOrNonRealState(token : String,  addUpdateRealStateParams: AddUpdateRealStateParams): Result<GenericAddUpdateAssetResponse> {
        return try {
            val newToken = "Bearer $token"
            val response:GenericAddUpdateAssetResponse = if(addUpdateRealStateParams.AssetTypeId == AppConstant.assetRealStateId)
                serverApi.addUpdateAssetsRealState( addUpdateRealStateParams)
            else
                serverApi.addUpdateAssetsNonRealState( addUpdateRealStateParams)
            Timber.e(response.toString())
            Result.Success(response)
        } catch (e: Throwable) {
            if (e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error notification -", e))
        }
    }

    suspend fun deleteAsset(token : String,  assetId:Int,
                              borrowerId:Int,
                              loanApplicationId:Int): Result<GenericAddUpdateAssetResponse> {
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.deleteAsset( AssetId = assetId, borrowerId = borrowerId, loanApplicationId = loanApplicationId)
            Timber.e("deleteAsset = $response")
            Result.Success(response)
        } catch (e: Throwable) {
            if (e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error notification -", e))
        }
    }





    suspend fun addUpdateRetirement(token : String, retirementAddUpdateParams: RetirementAddUpdateParams ): Result<GenericAddUpdateAssetResponse> {
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.addUpdateRetirement( retirementAddUpdateParams)
            Log.e("-addUpdateBankDetails----", response.toString())
            Result.Success(response)
        } catch (e: Throwable) {
            if (e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error notification -", e))
        }
    }



    suspend fun getBankAccountType(token: String) : Result<ArrayList<DropDownResponse>> {
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.getBankAccountType()
            Timber.e("BankAccountTyppes:  - $response")
            Result.Success(response)
        } catch (e: Throwable) {
            if (e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error notification -", e.cause))
        }
    }

    suspend fun getRetirementAccountDetails(
        token : String,
        loanApplicationId : Int,
        borrowerId : Int,
        borrowerAssetId : Int
    ): Result<RetirementAccountResponse> {
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.getRetirementAccountDetails( loanApplicationId,borrowerId,borrowerAssetId)
            Log.e("BankAccount-Details----", response.toString())
            Result.Success(response)
        } catch (e: Throwable) {
            if (e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error notification -", e))
        }
    }

    suspend fun getFinancialAssetDetails(
        token : String,
        loanApplicationId : Int,
        borrowerId : Int,
        borrowerAssetId : Int
    ): Result<FinancialAssetResponse> {
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.getFinancialAssetDetails(newToken, loanApplicationId,borrowerId,borrowerAssetId)
            Log.e("Financial Asset Response----", response.toString())
            Result.Success(response)
        } catch (e: Throwable) {
            if (e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error notification -", e))
        }
    }

    suspend fun getAllFinancialAssets(token: String) : Result<ArrayList<DropDownResponse>> {
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.getFinancialAsset()
            Timber.e("BankAccountTyppes:  - $response")
            Result.Success(response)
        } catch (e: Throwable) {
            if (e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error notification -", e.cause))
        }
    }

    suspend fun getFromLoanNonRealEstateDetail(
        token : String, loanApplicationId : Int, borrowerId : Int, AssetTypeId:Int, borrowerAssetId : Int): Result<AssetsRealEstateResponse> {
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.getFromLoanNonRealEstateDetail(newToken, loanApplicationId,borrowerId, AssetTypeId,borrowerAssetId)
            Log.e("Financial Asset Response----", response.toString())
            Result.Success(response)
        } catch (e: Throwable) {
            if (e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error notification -", e))
        }
    }

    suspend fun getFromLoanRealEstateDetail(
        token : String, loanApplicationId : Int, borrowerId : Int, AssetTypeId:Int, borrowerAssetId : Int): Result<AssetsRealEstateResponse> {
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.getFromLoanRealEstateDetail( loanApplicationId,borrowerId, AssetTypeId,borrowerAssetId)
            Log.e("Asset LoanReal Estate--", response.toString())
            Result.Success(response)
        } catch (e: Throwable) {
            if (e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error notification -", e))
        }
    }

    suspend fun getAssetByCategory(token: String, categoryId: Int, loanPurposeId:Int) : Result<ArrayList<AssetTypesByCategory>> {
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.getAssetTransactionType( categoryId, loanPurposeId)
            //Timber.e("Assets by category:  - $response")
            Result.Success(response)
        } catch (e: Throwable) {
            if (e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error notification -", e.cause))
        }
    }


    suspend fun getProceedFromLoan(
        token: String, loanApplicationId : Int, borrowerId : Int, AssetTypeId:Int, borrowerAssetId : Int): Result<ProceedFromLoanResponse> {
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.getProceedFromLoan( loanApplicationId,borrowerId, AssetTypeId,borrowerAssetId)
            Log.e("Proceed from loan--", response.toString())
            Result.Success(response)
        } catch (e: Throwable) {
            if (e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error notification -", e))
        }
    }

    suspend fun getGiftAsset(
        token: String, loanApplicationId: Int, borrowerId: Int, borrowerAssetId : Int): Result<GiftAssetResponse> {
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.getGiftAssetDetail( loanApplicationId,borrowerId,borrowerAssetId)
            //Log.e("Gift Asset Detail--", response.toString())
            Result.Success(response)
        } catch (e: Throwable) {
            if (e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error notification -", e))
        }
    }

    suspend fun getAllGiftSources(token: String): Result<ArrayList<GiftSourcesResponse>> {
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.getAllGiftSources()
            //Timber.e("Gift sources: - $response")
            Result.Success(response)
        } catch (e: Throwable) {
            if (e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error notification -", e.cause))
        }
    }

    suspend fun getOtherAssetDetails(
        token: String, loanApplicationId: Int, borrowerId: Int, borrowerAssetId : Int): Result<OtherAssetResponse> {
        return try {
            val newToken = "Bearer $token"
            val response = serverApi.getOtherAssetDetails( loanApplicationId,borrowerId,borrowerAssetId)
            Log.e("OtherAssetResponse--", response.toString())
            Result.Success(response)
        } catch (e: Throwable) {
            if (e is NoConnectivityException)
                Result.Error(IOException(AppConstant.INTERNET_ERR_MSG))
            else
                Result.Error(IOException("Error notification -", e))
        }
    }

}