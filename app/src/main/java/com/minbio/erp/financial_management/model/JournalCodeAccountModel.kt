package com.minbio.erp.financial_management.model

import android.os.Parcel
import android.os.Parcelable

data class JournalCodeAccountModel(
    val `data`: List<JournalCodeAccountData>,
    val message: String,
    val status: Boolean
)

data class JournalCodeAccountData(
    val code: String,
    val id: Int,
    val label: String,
    val journal_nature_id : Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(code)
        parcel.writeInt(id)
        parcel.writeString(label)
        parcel.writeInt(journal_nature_id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<JournalCodeAccountData> {
        override fun createFromParcel(parcel: Parcel): JournalCodeAccountData {
            return JournalCodeAccountData(parcel)
        }

        override fun newArray(size: Int): Array<JournalCodeAccountData?> {
            return arrayOfNulls(size)
        }
    }
}