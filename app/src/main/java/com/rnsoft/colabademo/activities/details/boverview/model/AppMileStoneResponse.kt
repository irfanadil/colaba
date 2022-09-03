package com.rnsoft.colabademo

import com.google.gson.annotations.SerializedName

data class AppMileStoneResponse(
    val code: String,
    @SerializedName("data") val  mileStoneData: ArrayList<MileStoneData>,
    val message: String,
    val status: String
)

data class MileStoneData(
    val createdDate: String?=null,
    val description: String?=null,
    val id: Int,
    val isCurrent: Boolean,
    val milestoneType: Int,
    val name: String
)