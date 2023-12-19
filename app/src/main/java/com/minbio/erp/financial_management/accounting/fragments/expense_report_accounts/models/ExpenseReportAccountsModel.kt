package com.minbio.erp.financial_management.accounting.fragments.expense_report_accounts.models

import android.os.Parcel
import android.os.Parcelable
import com.minbio.erp.corporate_management.models.Links
import com.minbio.erp.corporate_management.models.Meta

data class ExpenseReportAccountsModel(
    val `data`: List<ExpenseReportAccountsData>,
    val links: Links,
    val message: String,
    val meta: Meta,
    val status: Boolean
)

data class ExpenseReportAccountsData(
    val chart_account_id: Int,
    val chart_account_number: String = "",
    val chart_account_label: String = "",
    val code: String,
    val id: Int,
    val label: String,
    var status: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(chart_account_id)
        parcel.writeString(chart_account_number)
        parcel.writeString(chart_account_label)
        parcel.writeString(code)
        parcel.writeInt(id)
        parcel.writeString(label)
        parcel.writeInt(status)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ExpenseReportAccountsData> {
        override fun createFromParcel(parcel: Parcel): ExpenseReportAccountsData {
            return ExpenseReportAccountsData(parcel)
        }

        override fun newArray(size: Int): Array<ExpenseReportAccountsData?> {
            return arrayOfNulls(size)
        }
    }
}
