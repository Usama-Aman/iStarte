package com.minbio.erp.corporate_management.models

import android.os.Parcel
import android.os.Parcelable

data class CorporateUsers(
    val data: List<CorporateUsersData?>,
    val links: Links,
    val message: String = "",
    val meta: Meta,
    val status: Boolean
)

data class CorporateUsersData(
    val company: String,
    val company_id: Int,
    val country_code: String,
    val designation: String,
    val designation_id: Int,
    val email: String,
    val email_verified: Int,
    val first_name: String,
    val id: Int,
    val image_path: String,
    val is_active: Int,
    val iso2: String,
    val lang: String,
    val last_name: String,
    val phone: String,
    val phone_verified: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(company)
        parcel.writeInt(company_id)
        parcel.writeString(country_code)
        parcel.writeString(designation)
        parcel.writeInt(designation_id)
        parcel.writeString(email)
        parcel.writeInt(email_verified)
        parcel.writeString(first_name)
        parcel.writeInt(id)
        parcel.writeString(image_path)
        parcel.writeInt(is_active)
        parcel.writeString(iso2)
        parcel.writeString(lang)
        parcel.writeString(last_name)
        parcel.writeString(phone)
        parcel.writeInt(phone_verified)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CorporateUsersData> {
        override fun createFromParcel(parcel: Parcel): CorporateUsersData {
            return CorporateUsersData(parcel)
        }

        override fun newArray(size: Int): Array<CorporateUsersData?> {
            return arrayOfNulls(size)
        }
    }
}

data class Links(
    val first: String,
    val last: String,
    val next: Any,
    val prev: Any
)

data class Meta(
    val current_page: Int,
    val from: Int,
    val last_page: Int,
    val path: String,
    val per_page: Int,
    val to: Int,
    val total: Int
)