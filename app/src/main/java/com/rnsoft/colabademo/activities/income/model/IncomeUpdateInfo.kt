package com.rnsoft.colabademo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Anita Kiran on 11/29/2021.
 */

@Parcelize
data class IncomeUpdateInfo(
    var incomeUpdateBox : String,
    var incomeUpdateTab : Int
    ) : Parcelable
