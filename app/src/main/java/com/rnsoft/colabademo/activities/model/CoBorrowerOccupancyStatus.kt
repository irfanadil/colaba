package com.rnsoft.colabademo

import com.google.gson.annotations.SerializedName

/**
 * Created by Anita Kiran on 10/12/2021.
 */
data class CoBorrowerOccupancyStatus(
    val code: String,
    @SerializedName("data") val occupancyData : ArrayList<CoBorrowerOccupancyData>?,
    val message: String?,
    val status: String
)

data class CoBorrowerOccupancyData(
    var borrowerId : Int,
    var borrowerFirstName : String?,
    var borrowerLastName: String?,
    var willLiveInSubjectProperty: Boolean?
)


