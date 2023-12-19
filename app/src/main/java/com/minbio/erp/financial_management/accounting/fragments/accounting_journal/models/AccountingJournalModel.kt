package com.minbio.erp.financial_management.accounting.fragments.accounting_journal.models

import android.os.Parcel
import android.os.Parcelable
import com.minbio.erp.corporate_management.models.Links
import com.minbio.erp.corporate_management.models.Meta

data class AccountingJournalModel(
    val `data`: List<AccountingJournalData>,
    val status: Boolean,
    val links: Links,
    val message: String = "",
    val meta: Meta
)

data class AccountingJournalData(
    val code: String,
    val id: Int,
    val journal_nature: String,
    val label: String,
    var status: Int,
    val journal_nature_id : Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(code)
        parcel.writeInt(id)
        parcel.writeString(journal_nature)
        parcel.writeString(label)
        parcel.writeInt(status)
        parcel.writeInt(journal_nature_id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AccountingJournalData> {
        override fun createFromParcel(parcel: Parcel): AccountingJournalData {
            return AccountingJournalData(
                parcel
            )
        }

        override fun newArray(size: Int): Array<AccountingJournalData?> {
            return arrayOfNulls(size)
        }
    }
}