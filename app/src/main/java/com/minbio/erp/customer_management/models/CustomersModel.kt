package com.minbio.erp.customer_management.models

import android.os.Parcel
import android.os.Parcelable
import com.minbio.erp.corporate_management.models.Links
import com.minbio.erp.corporate_management.models.Meta

data class CustomersModel(
    val data: List<CustomersData>,
    val links: Links,
    val message: String,
    val meta: Meta,
    val status: Boolean
)

data class CustomersData(
    val address: String?,
    val company_name: String?,
    val country_code: String?,
    val email: String?,
    val email_verified: Int,
    val id: Int,
    val id_file_ext: String?,
    val id_file_path: String?,
    val is_active: Int,
    val is_merchant: Int,
    val iso2: String?,
    val kbis_file_ext: String?,
    val kbis_file_path: String?,
    val lang: String?,
    val last_name: String?,
    val lat: String?,
    val lng: String?,
    val name: String?,
    val nego_auto_accept: Int,
    val phone: String?,
    val phone_verified: Int,
    val siret_no: String?,
    val image_path: String?,
    val country_id: Int,
    val overdraft_amount: Int,
    val pending_overdraft: String?,
    val balance: String?,
    val payment_status: String?,
    val vat_id: String?,
    val is_allow_overdraft: Int,
    val is_overdraft_due: Int,
    val is_pending_overdraft: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(address)
        parcel.writeString(company_name)
        parcel.writeString(country_code)
        parcel.writeString(email)
        parcel.writeInt(email_verified)
        parcel.writeInt(id)
        parcel.writeString(id_file_ext)
        parcel.writeString(id_file_path)
        parcel.writeInt(is_active)
        parcel.writeInt(is_merchant)
        parcel.writeString(iso2)
        parcel.writeString(kbis_file_ext)
        parcel.writeString(kbis_file_path)
        parcel.writeString(lang)
        parcel.writeString(last_name)
        parcel.writeString(lat)
        parcel.writeString(lng)
        parcel.writeString(name)
        parcel.writeInt(nego_auto_accept)
        parcel.writeString(phone)
        parcel.writeInt(phone_verified)
        parcel.writeString(siret_no)
        parcel.writeString(image_path)
        parcel.writeInt(country_id)
        parcel.writeInt(overdraft_amount)
        parcel.writeString(pending_overdraft)
        parcel.writeString(balance)
        parcel.writeString(payment_status)
        parcel.writeString(vat_id)
        parcel.writeInt(is_allow_overdraft)
        parcel.writeInt(is_overdraft_due)
        parcel.writeInt(is_pending_overdraft)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CustomersData> {
        override fun createFromParcel(parcel: Parcel): CustomersData {
            return CustomersData(parcel)
        }

        override fun newArray(size: Int): Array<CustomersData?> {
            return arrayOfNulls(size)
        }
    }

}
