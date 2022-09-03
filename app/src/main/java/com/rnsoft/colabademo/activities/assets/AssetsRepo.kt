package com.rnsoft.colabademo

import com.rnsoft.AssetTypesByCategory
import com.rnsoft.colabademo.activities.assets.fragment.model.*
import retrofit2.Response
import retrofit2.http.*
import javax.inject.Inject

/**
 * Created by Anita Kiran on 11/1/2021.
 */

class AssetsRepo @Inject constructor(private val dataSource : AssetDataSource){

    suspend fun getBankAccountDetails(token: String, loanApplicationId : Int, borrowerId : Int, borrowerAssetId : Int): Result<BankAccountResponse> {
        return dataSource.getBankAccountDetails(token = token, loanApplicationId,borrowerId,borrowerAssetId)
    }

    suspend fun addUpdateBankDetails(token: String, bankAddUpdateParams: BankAddUpdateParams): Result<GenericAddUpdateAssetResponse> {
        return dataSource.addUpdateBankDetails(token = token, bankAddUpdateParams)
    }

    suspend fun addUpdateOtherAsset(token: String, otherAssetAddUpdateParams: OtherAssetAddUpdateParams): Result<GenericAddUpdateAssetResponse> {
        return dataSource.addUpdateOtherAsset(token = token, otherAssetAddUpdateParams)
    }

    suspend fun addUpdateGift(token: String, giftAddUpdateParams: GiftAddUpdateParams): Result<GenericAddUpdateAssetResponse> {
        return dataSource.addUpdateGift(token = token, giftAddUpdateParams)
    }


    suspend fun addUpdateRetirement(token: String,  retirementAddUpdateParams: RetirementAddUpdateParams ): Result<GenericAddUpdateAssetResponse> {
        return dataSource.addUpdateRetirement(token = token, retirementAddUpdateParams)
    }

    suspend fun addUpdateStockBonds( stocksBondsAddUpdateParams:StocksBondsAddUpdateParams): Result<GenericAddUpdateAssetResponse> {
        return dataSource.addUpdateStockBonds( stocksBondsAddUpdateParams)
    }




    suspend fun addUpdateProceedFromLoan(token: String, addUpdateProceedLoanParams: AddUpdateProceedLoanParams): Result<GenericAddUpdateAssetResponse> {
        return dataSource.addUpdateProceedFromLoan(token = token, addUpdateProceedLoanParams)
    }

    suspend fun addUpdateProceedFromLoanOther(token: String,  addUpdateProceedFromLoanOtherParams: AddUpdateProceedFromLoanOtherParams): Result<GenericAddUpdateAssetResponse> {
        return dataSource.addUpdateProceedFromLoanOther(token = token, addUpdateProceedFromLoanOtherParams)
    }

    suspend fun addUpdateAssetsRealStateOrNonRealState(token: String,  addUpdateRealStateParams: AddUpdateRealStateParams): Result<GenericAddUpdateAssetResponse> {
        return dataSource.addUpdateAssetsRealStateOrNonRealState(token = token, addUpdateRealStateParams)
    }



    suspend fun deleteAsset(token : String,  assetId:Int, borrowerId:Int, loanApplicationId:Int): Result<GenericAddUpdateAssetResponse>{
        return dataSource.deleteAsset(token = token, assetId = assetId, borrowerId = borrowerId, loanApplicationId = loanApplicationId)
    }



    suspend fun fetchAssetTypesByCategoryItemList(token: String , categoryId:Int, loanPurposeId:Int): Result<ArrayList<GetAssetTypesByCategoryItem>> {
        return dataSource.fetchAssetTypesByCategoryItemList(token = token , categoryId, loanPurposeId)
    }

    suspend fun getProceedsFromLoan(token: String, loanApplicationId : Int, borrowerId : Int, assetTypeID:Int, borrowerAssetId : Int ):Result<ProceedFromLoanModel>{
        return dataSource.getProceedsFromLoan(token = token , loanApplicationId , borrowerId , assetTypeID, borrowerAssetId )
    }

    suspend fun getProceedsFromNonRealEstateDetail(token: String, loanApplicationId : Int, borrowerId : Int, assetTypeID:Int, borrowerAssetId : Int ):Result<ProceedFromLoanModel>{
        return dataSource.getProceedsFromNonRealEstateDetail(token = token , loanApplicationId , borrowerId , assetTypeID, borrowerAssetId )
    }

    suspend fun getProceedsFromRealEstateDetail(token: String, loanApplicationId : Int, borrowerId : Int, assetTypeID:Int, borrowerAssetId : Int ):Result<ProceedFromLoanModel>{
        return dataSource.getProceedsFromRealEstateDetail(token = token , loanApplicationId , borrowerId , assetTypeID, borrowerAssetId )
    }

    suspend fun getBankAccountType(token: String): Result<ArrayList<DropDownResponse>> {
        return dataSource.getBankAccountType(token = token)
    }

    suspend fun getRetirementAccountDetails(token: String, loanApplicationId : Int, borrowerId : Int, borrowerAssetId : Int): Result<RetirementAccountResponse> {
        return dataSource.getRetirementAccountDetails(token = token, loanApplicationId,borrowerId,borrowerAssetId)
    }

    suspend fun getFinancialAssetDetail(token: String, loanApplicationId : Int, borrowerId : Int, borrowerAssetId : Int): Result<FinancialAssetResponse> {
        return dataSource.getFinancialAssetDetails(token = token, loanApplicationId,borrowerId,borrowerAssetId)
    }

    suspend fun getAllFinancialAsset(token: String): Result<ArrayList<DropDownResponse>> {
        return dataSource.getAllFinancialAssets(token = token)
    }

    suspend fun getFromLoanNonRealEstateDetail(token: String, loanApplicationId : Int, borrowerId : Int, assetTypeId:Int, borrowerAssetId : Int): Result<AssetsRealEstateResponse> {
        return dataSource.getFromLoanNonRealEstateDetail(token = token, loanApplicationId,borrowerId,assetTypeId,borrowerAssetId)
    }

    suspend fun getFromLoanRealEstateDetail(token: String, loanApplicationId : Int, borrowerId : Int, assetTypeId: Int, borrowerAssetId : Int): Result<AssetsRealEstateResponse> {
        return dataSource.getFromLoanRealEstateDetail(token = token, loanApplicationId,borrowerId,assetTypeId, borrowerAssetId)
    }

    suspend fun getAssetByCategory(token: String, categoryId: Int, loanPurposeId: Int): Result<ArrayList<AssetTypesByCategory>> {
        return dataSource.getAssetByCategory(token,categoryId,loanPurposeId)
    }

    suspend fun getProceedFromLoan(token: String, loanApplicationId : Int, borrowerId : Int, assetTypeId: Int, borrowerAssetId : Int): Result<ProceedFromLoanResponse> {
        return dataSource.getProceedFromLoan(token = token, loanApplicationId,borrowerId,assetTypeId, borrowerAssetId)
    }

    suspend fun getGiftAsset(token: String, loanApplicationId : Int, borrowerId : Int, borrowerAssetId : Int): Result<GiftAssetResponse> {
        return dataSource.getGiftAsset(token = token, loanApplicationId,borrowerId, borrowerAssetId)
    }

    suspend fun getAllGiftSources(token: String): Result<ArrayList<GiftSourcesResponse>> {
        return dataSource.getAllGiftSources(token = token)
    }

    suspend fun getOtherAsset(token: String, loanApplicationId : Int, borrowerId : Int, borrowerAssetId : Int): Result<OtherAssetResponse> {
        return dataSource.getOtherAssetDetails(token = token, loanApplicationId,borrowerId, borrowerAssetId)
    }




}