package com.minbio.erp.financial_management.bank_cash.fragments.financial.models

import android.os.Parcel
import android.os.Parcelable
import com.minbio.erp.corporate_management.models.Links
import com.minbio.erp.corporate_management.models.Meta

data class FinancialAccountListModel(
    val `data`: List<FinancialAccountListData>,
    val message: String,
    val links: Links,
    val meta: Meta,
    val status: Boolean
)

data class FinancialAccountListData(
    val account_holder: String,
    val account_holder_address: String,
    val account_no: String,
    val account_type: String,
    val chart_account_id: Int,
    val bank_address: String,
    val bank_code: String,
    val bank_name: String,
    val bic_swift_code: String,
    val comment: String,
    val country: String,
    val currency: String,
    val date: String,
    val iban: String,
    val id: Int,
    val initial_balance: String,
    val label: String,
    val minimum_allowed_balance: String,
    val minimum_desired_balance: String,
    val reference: String,
    val state: String,
    val status: String,
    val web: String,
    val entries_to_reconcile: String,
    val accounting_journal_id: Int,
    val currency_id: Int,
    val country_id: Int,
    val bank_lat: String,
    val bank_lng: String,
    val owner_lat: String,
    val owner_lng: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(account_holder)
        parcel.writeString(account_holder_address)
        parcel.writeString(account_no)
        parcel.writeString(account_type)
        parcel.writeInt(chart_account_id)
        parcel.writeString(bank_address)
        parcel.writeString(bank_code)
        parcel.writeString(bank_name)
        parcel.writeString(bic_swift_code)
        parcel.writeString(comment)
        parcel.writeString(country)
        parcel.writeString(currency)
        parcel.writeString(date)
        parcel.writeString(iban)
        parcel.writeInt(id)
        parcel.writeString(initial_balance)
        parcel.writeString(label)
        parcel.writeString(minimum_allowed_balance)
        parcel.writeString(minimum_desired_balance)
        parcel.writeString(reference)
        parcel.writeString(state)
        parcel.writeString(status)
        parcel.writeString(web)
        parcel.writeString(entries_to_reconcile)
        parcel.writeInt(accounting_journal_id)
        parcel.writeInt(currency_id)
        parcel.writeInt(country_id)
        parcel.writeString(bank_lat)
        parcel.writeString(bank_lng)
        parcel.writeString(owner_lat)
        parcel.writeString(owner_lng)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FinancialAccountListData> {
        override fun createFromParcel(parcel: Parcel): FinancialAccountListData {
            return FinancialAccountListData(parcel)
        }

        override fun newArray(size: Int): Array<FinancialAccountListData?> {
            return arrayOfNulls(size)
        }
    }
}