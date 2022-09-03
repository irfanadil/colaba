package com.rnsoft.colabademo


data class TestAssetsModelClass(
    val headerTitle: String = "Header Title",
    val headerAmount: String = "$0",
    val footerTitle: String  = "Default Footer Title",
    val contentCell:ArrayList<TestContentCell>,
    val listenerAttached: Int = 0
)

data class TestContentCell(val title:String="Title", val description:String="detail", val contentAmount:String="0$")


/*
data class AssetsModelDataClass(
    val code: String?=null,
    @SerializedName("data") val bAssetData : BAssetData?=null,
    val message: String?=null,
    val status: String?=null
)

data class BAssetData(
    @SerializedName("Borrower") val assetBorrowerList :ArrayList<AssetBorrowerList>,
    val totalAssetsValue: Double
)


data class AssetBorrowerList(
    val assetsValue: Double,
    @SerializedName("BorrowerAsset") val bAssets:ArrayList<BAssets>,
    //val borrowerAssets: ArrayList<BorrowerAsset>,
    val borrowerId: Int,
    val borrowerName: String,
    val ownTypeDisplayName: String,
    val ownTypeId: Int,
    val ownTypeName: String
)


data class BAssets(
    val assetCategoryId: Int,
    val assetCategoryName: String,
    val assetId: Int,
    val assetName: String,
    val assetTypeID: Int,
    val assetTypeName: String,
    val assetValue: Double,
    val isDisabledByTenant: Boolean,
    val isEarnestMoney: Boolean
)

 */