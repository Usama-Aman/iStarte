package com.minbio.erp.supplier_management.models

import android.os.Parcel
import android.os.Parcelable
import com.minbio.erp.corporate_management.models.Links
import com.minbio.erp.corporate_management.models.Meta

data class SuppliersModel(
    val data: List<SuppliersData>,
    val links: Links,
    val message: String,
    val meta: Meta,
    val status: Boolean
)

data class SuppliersData(
    val allowed_overdraft: String,
    val company_id: Int,
    val company_name: String,
    val contact_full_name: String,
    val country_code: String,
    val country_id: Int,
    val email: String,
    val id: Int,
    val id_file_ext: String,
    val id_file_path: String,
    val image_path: String,
    val is_active: Int,
    val iso2: String,
    val kbis_file_ext: String,
    val kbis_file_path: String,
    val phone: String,
    val siret_no: String,
    val address: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(allowed_overdraft)
        parcel.writeInt(company_id)
        parcel.writeString(company_name)
        parcel.writeString(contact_full_name)
        parcel.writeString(country_code)
        parcel.writeInt(country_id)
        parcel.writeString(email)
        parcel.writeInt(id)
        parcel.writeString(id_file_ext)
        parcel.writeString(id_file_path)
        parcel.writeString(image_path)
        parcel.writeInt(is_active)
        parcel.writeString(iso2)
        parcel.writeString(kbis_file_ext)
        parcel.writeString(kbis_file_path)
        parcel.writeString(phone)
        parcel.writeString(siret_no)
        parcel.writeString(address)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SuppliersData> {
        override fun createFromParcel(parcel: Parcel): SuppliersData {
            return SuppliersData(parcel)
        }

        override fun newArray(size: Int): Array<SuppliersData?> {
            return arrayOfNulls(size)
        }
    }
}