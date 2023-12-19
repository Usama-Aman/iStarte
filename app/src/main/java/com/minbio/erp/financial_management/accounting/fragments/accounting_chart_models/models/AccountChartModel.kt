package com.minbio.erp.financial_management.accounting.fragments.accounting_chart_models.models

import android.os.Parcel
import android.os.Parcelable
import com.minbio.erp.corporate_management.models.Links
import com.minbio.erp.corporate_management.models.Meta

data class AccountChartModel(
    val `data`: List<AccountChartModelData>,
    val links: Links,
    val meta: Meta,
    val message: String,
    val status: Boolean
)

data class AccountChartModelData(
    val chart_accounts_model: String,
    val chart_accounts_model_id: Int,
    val country: String,
    val country_id: Int,
    val id: Int,
    val label: String,
    var status: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(chart_accounts_model)
        parcel.writeInt(chart_accounts_model_id)
        parcel.writeString(country)
        parcel.writeInt(country_id)
        parcel.writeInt(id)
        parcel.writeString(label)
        parcel.writeInt(status)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AccountChartModelData> {
        override fun createFromParcel(parcel: Parcel): AccountChartModelData {
            return AccountChartModelData(parcel)
        }

        override fun newArray(size: Int): Array<AccountChartModelData?> {
            return arrayOfNulls(size)
        }
    }
}