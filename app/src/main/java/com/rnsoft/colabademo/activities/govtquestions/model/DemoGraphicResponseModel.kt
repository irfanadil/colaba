package com.rnsoft.colabademo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class DemoGraphicResponseModel(
    var code: String?,
    @SerializedName("data") var demoGraphicData : DemoGraphicData?,
    var message: String?,
    var status: String?,
    var passedBorrowerId:Int?
)

data class DemoGraphicData(
    var borrowerId: Int?,
    var ethnicity: ArrayList<EthnicityDemoGraphic>? = arrayListOf(),
    var genderId: Int?,
    var loanApplicationId: Int?,
    var race: ArrayList<DemoGraphicRace>? = arrayListOf() ,
    var state: String?
)

data class EthnicityDemoGraphic(
    var ethnicityDetails: ArrayList<EthnicityDetailDemoGraphic>? = arrayListOf(),
    var ethnicityId: Int?
)

@Parcelize
data class EthnicityDetailDemoGraphic(
    var detailId: Int?,
    var name: String?,
    var isOther: Boolean?,
    var otherEthnicity: String?
):Parcelable


data class DemoGraphicRace(
    var raceDetails : ArrayList<DemoGraphicRaceDetail>? = arrayListOf(),
    var raceId: Int?
)

@Parcelize
data class DemoGraphicRaceDetail(
    var detailId: Int?,
    var name: String?,
    var isOther: Boolean?,
    var otherRace: String?
):Parcelable

