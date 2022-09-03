package com.rnsoft.colabademo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AssetReturnParams(
    val assetCategoryId: Int? = null,
    val assetCategoryName: String? = null,
    val assetBorrowerName: String? = null,
    val assetId: Int? = null,
    val assetName: String? = null,
    val assetTypeID: Int? = null,
    var assetUniqueId: Int? = null,
    val assetTypeName: String? = "",
    val assetValue: Double? = null,
    val listenerAttached:Int? = null,
    var assetAction:String = AppConstant.assetDeleted

): Parcelable

