package com.rnsoft.colabademo

import android.os.Parcel
import android.os.Parcelable
import androidx.versionedparcelable.VersionedParcelize

//data class AllLoansArrayList(val loanItem:ArrayList<LoanItem>?)

@VersionedParcelize
data class LoanItem(
    var activityTime: String? = "",
    var cellNumber: String? = "",
    var coBorrowerCount: Int? = null,
    var detail: Detail? = null,
    var documents: Int?  = null,
    var email: String?  = null,
    var firstName: String?  = null,
    var lastName: String?  = null,
    var loanApplicationId: Int?  = null,
    var loanPurpose: String?  = null,
    var milestone: String?  = null,
    var recycleCardState:Boolean? = false,

    ): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readParcelable(Detail::class.java.classLoader),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(activityTime)
        parcel.writeString(cellNumber)
        if (coBorrowerCount != null) {
            parcel.writeInt(coBorrowerCount!!)
        }
        parcel.writeParcelable(detail, flags)
        if (documents != null) {
            parcel.writeInt(documents!!)
        }
        parcel.writeString(email)
        parcel.writeString(firstName)
        parcel.writeString(lastName)
        if (loanApplicationId != null) {
            parcel.writeInt(loanApplicationId!!)
        }
        parcel.writeString(loanPurpose)
        parcel.writeString(milestone)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LoanItem> {
        override fun createFromParcel(parcel: Parcel): LoanItem {
            return LoanItem(parcel)
        }

        override fun newArray(size: Int): Array<LoanItem?> {
            return arrayOfNulls(size)
        }
    }
}


@VersionedParcelize
data class Detail(
    var address: Address?,
    var loanAmount: Int?,
    var propertyValue: Int?
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(Address::class.java.classLoader),
        parcel.readInt(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(address, flags)
        if (loanAmount != null) {
            parcel.writeInt(loanAmount!!)
        }
        if (propertyValue != null) {
            parcel.writeInt(propertyValue!!)
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Detail> {
        override fun createFromParcel(parcel: Parcel): Detail {
            return Detail(parcel)
        }

        override fun newArray(size: Int): Array<Detail?> {
            return arrayOfNulls(size)
        }
    }
}


@VersionedParcelize
data class Address(
    var city: String?,
    var countryId: Int?,
    var countryName: String?,
    var stateId: Int?,
    var stateName: String?,
    var street: String?,
    var unit: String?,
    var zipCode: String?
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(city)
        if (countryId != null) {
            parcel.writeInt(countryId!!)
        }
        parcel.writeString(countryName)
        if (stateId != null) {
            parcel.writeInt(stateId!!)
        }
        parcel.writeString(stateName)
        parcel.writeString(street)
        parcel.writeString(unit)
        parcel.writeString(zipCode)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Address> {
        override fun createFromParcel(parcel: Parcel): Address {
            return Address(parcel)
        }

        override fun newArray(size: Int): Array<Address?> {
            return arrayOfNulls(size)
        }
    }
}