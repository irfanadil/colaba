package com.rnsoft.colabademo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

class GetTemplatesResponse : ArrayList<GetTemplatesResponseItem>()

data class GetTemplatesResponseItem(
    val docs: ArrayList<Doc>,
    val id: String,
    val name: String,
    val type: String
)

@Parcelize
data class Doc(
        var docType: String,
        var docMessage: String,
        val docTypeId: String?=null
):Parcelable