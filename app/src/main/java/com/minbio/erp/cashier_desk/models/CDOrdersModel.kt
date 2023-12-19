package com.minbio.erp.cashier_desk.models

import android.os.Parcel
import android.os.Parcelable
import com.minbio.erp.corporate_management.models.Links
import com.minbio.erp.corporate_management.models.Meta

data class CDOrdersModel(
    val `data`: List<CDOrderData>,
    val links: Links,
    val message: String,
    val meta: Meta,
    val status: Boolean
)

data class CDOrderData(
    val date: String,
    val details: CDOrderDetail,
    val id: Int,
    val order_no: String,
    val status: String
)

data class CDOrderDetail(
    val credit_note: String,
    val delivery_charges: String,
    val discount: String,
    val grandtotal: String,
    val id: Int,
    val order_no: String,
    val subtotal: String,
    val vat: String,
    val customer_name: String,
    val customer_siret_no: String,
    val customer_image_path: String,
    val customer_balance: String,
    val customer_pending_overdraft: String,
    val payment_status: String,
    val customer_id: String
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
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(credit_note)
        parcel.writeString(delivery_charges)
        parcel.writeString(discount)
        parcel.writeString(grandtotal)
        parcel.writeInt(id)
        parcel.writeString(order_no)
        parcel.writeString(subtotal)
        parcel.writeString(vat)
        parcel.writeString(customer_name)
        parcel.writeString(customer_siret_no)
        parcel.writeString(customer_image_path)
        parcel.writeString(customer_balance)
        parcel.writeString(customer_pending_overdraft)
        parcel.writeString(payment_status)
        parcel.writeString(customer_id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CDOrderDetail> {
        override fun createFromParcel(parcel: Parcel): CDOrderDetail {
            return CDOrderDetail(parcel)
        }

        override fun newArray(size: Int): Array<CDOrderDetail?> {
            return arrayOfNulls(size)
        }
    }

}
