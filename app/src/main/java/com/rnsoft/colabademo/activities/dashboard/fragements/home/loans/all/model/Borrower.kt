package com.rnsoft.colabademo

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import androidx.versionedparcelable.VersionedParcelize
import com.rnsoft.colabademo.R

@VersionedParcelize
data class Borrower(
    val id: Int,
    val banner: Int,
    val borrowerName: String?,
    val subtitle: String?,
    val about: String?
) :Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

   override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeInt(banner)
        parcel.writeString(borrowerName)
        parcel.writeString(subtitle)
        parcel.writeString(about)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Borrower> {
        override fun createFromParcel(parcel: Parcel): Borrower {
            return Borrower(parcel)
        }

        override fun newArray(size: Int): Array<Borrower?> {
            return arrayOfNulls(size)
        }

        fun customersList(context: Context): List<Borrower> {
            return listOf(
                Borrower(0, R.drawable.bird,
                    context.getString(R.string.title_rugby),
                    context.getString(R.string.subtitle_rugby),
                    context.getString(R.string.subtitle_rugby)
                ),
                Borrower(1, R.drawable.dog,
                    context.getString(R.string.title_cricket),
                    context.getString(R.string.subtitle_cricket),
                    context.getString(R.string.subtitle_cricket)
                ),
                Borrower(2, R.drawable.cat,
                    context.getString(R.string.title_hockey),
                    context.getString(R.string.subtitle_hockey),
                    context.getString(R.string.subtitle_hockey)
                ),
                Borrower(3, R.drawable.dollar_bag,
                    context.getString(R.string.title_basketball),
                    context.getString(R.string.subtitle_basketball),
                    context.getString(R.string.subtitle_basketball)
                ),
                Borrower(4, R.drawable.cat,
                    context.getString(R.string.title_hockey),
                    context.getString(R.string.subtitle_hockey),
                    context.getString(R.string.subtitle_hockey)
                ),
                Borrower(5, R.drawable.dollar_bag,
                    context.getString(R.string.title_basketball),
                    context.getString(R.string.subtitle_basketball),
                    context.getString(R.string.subtitle_basketball)
                ),
                Borrower(6, R.drawable.cat,
                    context.getString(R.string.title_hockey),
                    context.getString(R.string.subtitle_hockey),
                    context.getString(R.string.subtitle_hockey)
                ),
                Borrower(7, R.drawable.dollar_bag,
                    context.getString(R.string.title_basketball),
                    context.getString(R.string.subtitle_basketball),
                    context.getString(R.string.subtitle_basketball)
                ),
                Borrower(8, R.drawable.cat,
                    context.getString(R.string.title_hockey),
                    context.getString(R.string.subtitle_hockey),
                    context.getString(R.string.subtitle_hockey)
                ),
                Borrower(9, R.drawable.dollar_bag,
                    context.getString(R.string.title_basketball),
                    context.getString(R.string.subtitle_basketball),
                    context.getString(R.string.subtitle_basketball)
                ),
                Borrower(10, R.drawable.cat,
                    context.getString(R.string.title_hockey),
                    context.getString(R.string.subtitle_hockey),
                    context.getString(R.string.subtitle_hockey)
                ),
                Borrower(11, R.drawable.dollar_bag,
                    context.getString(R.string.title_basketball),
                    context.getString(R.string.subtitle_basketball),
                    context.getString(R.string.subtitle_basketball)
                ),
                Borrower(12, R.drawable.cat,
                    context.getString(R.string.title_hockey),
                    context.getString(R.string.subtitle_hockey),
                    context.getString(R.string.subtitle_hockey)
                ),
                Borrower(13, R.drawable.dollar_bag,
                    context.getString(R.string.title_basketball),
                    context.getString(R.string.subtitle_basketball),
                    context.getString(R.string.subtitle_basketball)
                )
            )
        }

        fun emptyCustomersList(context: Context): List<Borrower>  = listOf()

    }
}



