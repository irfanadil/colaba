package com.rnsoft.colabademo

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import androidx.versionedparcelable.VersionedParcelize

//data class AllLoansArrayList(val loanItem:ArrayList<LoanItem>?)

@VersionedParcelize
data class DocItem(
    var docUploadedTime: String? = "",
    var docType: String? = "",
    var docOneName: String?  = null,
    var docOneImage: String?  = null,
    var docTwoName: String?  = null,
    var docTwoImage: String?  = null,
    var docSetId: Int?  = null,
    var totalDocs: Int?  = null
    ): Parcelable {


    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(docUploadedTime)
        parcel.writeString(docType)
        parcel.writeString(docOneName)
        parcel.writeString(docOneImage)
        parcel.writeString(docTwoName)
        parcel.writeString(docTwoImage)
        parcel.writeValue(docSetId)
        parcel.writeValue(totalDocs)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DocItem> {
        override fun createFromParcel(parcel: Parcel): DocItem {
            return DocItem(parcel)
        }

        override fun newArray(size: Int): Array<DocItem?> {
            return arrayOfNulls(size)
        }

        fun testDocList(): ArrayList<DocItem> {

            val testArray: ArrayList<DocItem> = ArrayList()

            testArray.add(
                DocItem(
                    "Yesterday, 8:32 PM", "Bank Statements",
                    "Bank-S..0-1.pdf", "pdf",
                    "Bank-S..0-2.jpg", "pdf", 0, 4
                )
            )

            testArray.add(
                DocItem(
                    "Yesterday, 8:32 PM", "W-2s 2018",
                    "W-2s_2018.pdf", "pdf",
                    "", "", 1, 1
                )
            )

            testArray.add(
                DocItem(
                    "Yesterday, 8:32 PM", "Home Insurance",
                    "", "",
                    "", "", 2, 0
                )
            )

            testArray.add(
                DocItem(
                    "Yesterday, 8:32 PM", "Tax Transcripts",
                    "Tax-file-..34.pdf", "pdf",
                    "Tax-tran..01.pdf", "pdf", 3, 1
                )
            )

            return testArray
        }
    }




}

