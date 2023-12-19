package com.minbio.erp.financial_management.accounting.fragments.chart_accounts.models

import android.os.Parcel
import android.os.Parcelable
import com.minbio.erp.corporate_management.models.Links
import com.minbio.erp.corporate_management.models.Meta

data class ChartAccountModel(
    val `data`: List<ChartAccountData>,
    val links: Links,
    val message: String,
    val meta: Meta,
    val status: Boolean
)

data class ChartAccountData(
    val account_group: String,
    val account_number: String,
    val parent_account_number: String,
    val chart_accounts_model: String,
    val chart_accounts_model_id: Int,
    val id: Int,
    val label: String,
    val parent_id: Int,
    val short_label: String,
    var status: Int,
    var personalized_group_id: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(account_group)
        parcel.writeString(account_number)
        parcel.writeString(parent_account_number)
        parcel.writeString(chart_accounts_model)
        parcel.writeInt(chart_accounts_model_id)
        parcel.writeInt(id)
        parcel.writeString(label)
        parcel.writeInt(parent_id)
        parcel.writeString(short_label)
        parcel.writeInt(status)
        parcel.writeInt(personalized_group_id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ChartAccountData> {
        override fun createFromParcel(parcel: Parcel): ChartAccountData {
            return ChartAccountData(parcel)
        }

        override fun newArray(size: Int): Array<ChartAccountData?> {
            return arrayOfNulls(size)
        }
    }
}

