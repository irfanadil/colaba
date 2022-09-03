package com.rnsoft.colabademo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Anita Kiran on 10/26/2021.
 */
data class EthnicityResponseModel(
    val id: Int,
    val name: String,
    val ethnicityDetails: ArrayList<EthnicityDetails>
)

@Parcelize
data class EthnicityDetails(
    val id: Int?,
    val isOther : Boolean?,
    val name: String?,
    val otherPlaceHolder: String?,
) : Parcelable

