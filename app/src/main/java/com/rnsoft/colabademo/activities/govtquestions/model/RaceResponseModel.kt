package com.rnsoft.colabademo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


data class RaceResponseModel(
    val id: Int,
    val name: String?,
    val raceDetails: ArrayList<AllRaceDetails>
)

@Parcelize
data class AllRaceDetails(
   val id: Int,
   val isOther: Boolean?,
   val name: String?,
   val otherPlaceHolder: String?,
   var isChecked : Boolean?
) : Parcelable

