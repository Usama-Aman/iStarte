package com.minbio.erp.human_resources.models

import android.os.Parcel
import android.os.Parcelable
import com.minbio.erp.corporate_management.models.Links
import com.minbio.erp.corporate_management.models.Meta

data class LeavesListModel(
    val `data`: List<LeavesListData>,
    val message: String,
    val links: Links,
    val meta: Meta,
    val status: Boolean,
    val company_users: ArrayList<LeaveCompanyUser>

)

data class LeavesListData(
    val company_user_id: Int,
    val days: Int,
    val end_date: String,
    val end_half: String,
    val id: Int,
    val leave_type: String,
    val start_date: String,
    val start_half: String,
    val company_user_name: String,
    val date: String,
    val aproved_by: String,
    val assigned_to_id: Int,
    val note: String,
    val status: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(company_user_id)
        parcel.writeInt(days)
        parcel.writeString(end_date)
        parcel.writeString(end_half)
        parcel.writeInt(id)
        parcel.writeString(leave_type)
        parcel.writeString(start_date)
        parcel.writeString(start_half)
        parcel.writeString(company_user_name)
        parcel.writeString(date)
        parcel.writeString(aproved_by)
        parcel.writeInt(assigned_to_id)
        parcel.writeString(note)
        parcel.writeString(status)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LeavesListData> {
        override fun createFromParcel(parcel: Parcel): LeavesListData {
            return LeavesListData(parcel)
        }

        override fun newArray(size: Int): Array<LeavesListData?> {
            return arrayOfNulls(size)
        }
    }

}

data class LeaveCompanyUser(
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

    companion object CREATOR : Parcelable.Creator<LeaveCompanyUser> {
        override fun createFromParcel(parcel: Parcel): LeaveCompanyUser {
            return LeaveCompanyUser(parcel)
        }

        override fun newArray(size: Int): Array<LeaveCompanyUser?> {
            return arrayOfNulls(size)
        }
    }
}