package com.minbio.erp.human_resources.models

import android.os.Parcel
import android.os.Parcelable
import com.minbio.erp.corporate_management.models.Links
import com.minbio.erp.corporate_management.models.Meta

data class ExpensesListModel(
    val company_users: ArrayList<ExpensesCompanyUser>,
    val `data`: List<ExpensesListData>,
    val message: String,
    val status: Boolean,
    val links: Links,
    val meta: Meta
)

data class ExpensesCompanyUser(
    val first_name: String,
    val id: Int,
    val image: String,
    val image_path: String,
    val last_name: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(first_name)
        parcel.writeInt(id)
        parcel.writeString(image)
        parcel.writeString(image_path)
        parcel.writeString(last_name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ExpensesCompanyUser> {
        override fun createFromParcel(parcel: Parcel): ExpensesCompanyUser {
            return ExpensesCompanyUser(parcel)
        }

        override fun newArray(size: Int): Array<ExpensesCompanyUser?> {
            return arrayOfNulls(size)
        }
    }
}

data class ExpensesListData(
    val amount: String,
    val approvel_date: String,
    val aproved_by: String,
    val company_user_id: Int,
    val company_user_name: String,
    val date: String,
    val id: Int,
    val note: String,
    val status: String,
    val validation_date: String,
    val assigned_to_id: Int,
    val vat_account_id: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(amount)
        parcel.writeString(approvel_date)
        parcel.writeString(aproved_by)
        parcel.writeInt(company_user_id)
        parcel.writeString(company_user_name)
        parcel.writeString(date)
        parcel.writeInt(id)
        parcel.writeString(note)
        parcel.writeString(status)
        parcel.writeString(validation_date)
        parcel.writeInt(assigned_to_id)
        parcel.writeInt(vat_account_id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ExpensesListData> {
        override fun createFromParcel(parcel: Parcel): ExpensesListData {
            return ExpensesListData(parcel)
        }

        override fun newArray(size: Int): Array<ExpensesListData?> {
            return arrayOfNulls(size)
        }
    }
}