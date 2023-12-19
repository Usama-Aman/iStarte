package com.minbio.erp.financial_management.accounting.fragments.vat_accounts.models

import android.os.Parcel
import android.os.Parcelable
import com.minbio.erp.corporate_management.models.Links
import com.minbio.erp.corporate_management.models.Meta

data class VatAccountsModel(
    val `data`: List<VatAccountsData>,
    val links: Links,
    val message: String,
    val meta: Meta,
    val status: Boolean
)

data class VatAccountsData(
    val code: String,
    val country: String,
    val country_id: Int,
    val id: Int,
    val include_tax_2: Int,
    val include_tax_3: Int,
    val note: String,
    val npr: Int,
    val purchase_chart_account_id: Int,
    val rate: String,
    val rate_2: String,
    val rate_3: String,
    val sale_chart_account_id: Int,
    var status: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(code)
        parcel.writeString(country)
        parcel.writeInt(country_id)
        parcel.writeInt(id)
        parcel.writeInt(include_tax_2)
        parcel.writeInt(include_tax_3)
        parcel.writeString(note)
        parcel.writeInt(npr)
        parcel.writeInt(purchase_chart_account_id)
        parcel.writeString(rate)
        parcel.writeString(rate_2)
        parcel.writeString(rate_3)
        parcel.writeInt(sale_chart_account_id)
        parcel.writeInt(status)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VatAccountsData> {
        override fun createFromParcel(parcel: Parcel): VatAccountsData {
            return VatAccountsData(parcel)
        }

        override fun newArray(size: Int): Array<VatAccountsData?> {
            return arrayOfNulls(size)
        }
    }
}
