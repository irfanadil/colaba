package com.rnsoft.colabademo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue


@Parcelize
data class BorrowerDocsModel(
    val createdOn: String?,
    val docId: String?,
    val docName: String?,
    @SerializedName("files")
    val subFiles : @RawValue ArrayList<SubFiles>?=null,
    val id: String?,
    val requestId: String?,
    val status: String?,
    val typeId: String?,
    val userName: String?,
    val message:String?
    ) : Parcelable

@Parcelize
data class SubFiles(
    val byteProStatus: String?=null,
    val clientName: String,
    val fileModifiedOn: String?=null,
    val fileUploadedOn: String,
    val id: String,
    val isRead: Boolean=true,
    val mcuName: String,
    val status: String?=null,
    val userId: Int?=null,
    val userName: String?=null,

) : Parcelable