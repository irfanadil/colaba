package com.rnsoft.colabademo

import android.view.View
import com.google.gson.annotations.SerializedName
import com.rnsoft.colabademo.R


data class MyAssetBorrowerDataClass(
    val code: String?,
    @SerializedName("data") val bAssetData : BAssetData?=null,
    val message: String?,
    val status: String?,
    var passedBorrowerId:Int?,
    var updateBorrowerId:Int = -1,
    var visibleCategoryName:String = "TingPing"
    //var refreshTab:Boolean = true
)


data class BAssetData(
    val borrower: ABorrower?
)

data class ABorrower(
    val assetsTotal: Double?,
    val borrowerAssets: ArrayList<BorrowerAsset>?,
    val borrowerId: Int?,
    val borrowerName: String?,
    val ownTypeDisplayName: String?,
    val ownTypeId: Int?,
    val ownTypeName: String?
)

data class BorrowerAsset(
    val assets: ArrayList<Asset>?,
    val assetsCategory: String?,
    val assetsTotal: Double?,
    val listenerAttached: View.OnClickListener = View.OnClickListener {  },
    val listenerResource:Int =
       when(assetsCategory){
           CATEGORIES.BankAccount.categoryName ->{ CATEGORIES.BankAccount.returnMatchNavigation() }
           CATEGORIES.RetirementAccount.categoryName ->{
               CATEGORIES.RetirementAccount.returnMatchNavigation() }
           CATEGORIES.StocksBondsOtherFinancialAssets.categoryName ->{
               CATEGORIES.StocksBondsOtherFinancialAssets.returnMatchNavigation() }
           CATEGORIES.ProceedFromTransaction.categoryName ->{ CATEGORIES.ProceedFromTransaction.returnMatchNavigation() }
           CATEGORIES.GiftFunds.categoryName ->{ CATEGORIES.GiftFunds.returnMatchNavigation() }
           CATEGORIES.Other.categoryName ->{
               CATEGORIES.Other.returnMatchNavigation() }
           else -> 0
       })

enum class CATEGORIES(var categoryName: String){
    BankAccount("Bank Account"){
         override fun returnMatchNavigation(): Int = R.id.action_assets_bank_account

    },
    RetirementAccount("Retirement Account"){
        override fun returnMatchNavigation(): Int = R.id.action_assets_retirement
    },
    StocksBondsOtherFinancialAssets("Stocks, Bonds, Or Other Financial Assets"){
        override fun returnMatchNavigation(): Int = R.id.action_assets_stocks_bond
    },
    ProceedFromTransaction("Proceeds from Transactions"){
        override fun returnMatchNavigation(): Int = R.id.action_assets_proceeds_transaction

    },
    GiftFunds("Gift Funds"){
        override fun returnMatchNavigation(): Int =  R.id.action_assets_gift
    },
    Other("Other"){
        override fun returnMatchNavigation(): Int = R.id.action_assets_other

    };
    //abstract fun findRelevantCategory(toStringValue:String):Boolean  {  return categoryName.contains(toStringValue) }

    abstract fun returnMatchNavigation():Int
}



data class Asset(
    val assetCategoryId: Int?,
    val assetCategoryName: String?,
    @SerializedName("assetId") val assetUniqueId : Int,
    val assetName: String?,
    val assetTypeID: Int?,
    val assetTypeName: String?,
    val assetValue: Double?,
    val isDisabledByTenant: Boolean?,
    val isEarnestMoney: Boolean?
)

    /*
    private val navigateToBank = R.id.action_assets_bank_account
    private val navigateToRetirement = R.id.action_assets_retirement
    private val navigateToStockBonds = R.id.action_assets_stocks_bond
    private val navigateToTransactionAsset = R.id.action_assets_proceeds_transaction
    private val navigateToGiftAsset = R.id.action_assets_gift
    private val navigateToOtherAsset = R.id.action_assets_other
     */