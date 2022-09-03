package com.rnsoft.colabademo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Anita Kiran on 11/2/2021.
 */
@Parcelize
data class CommonAddressModel(
    var street : String?,
    var unit : String?,
    var city : String?,
    var stateId : Int?,
    var zipCode : String?,
    var countryId : Int?,
    var countryName : String?,
    var stateName : String?,
    var countyId: Int?,
    var countyName : String? = null
) : Parcelable
