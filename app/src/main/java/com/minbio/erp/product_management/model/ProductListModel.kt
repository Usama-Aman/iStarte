package com.minbio.erp.product_management.model

import android.os.Parcel
import android.os.Parcelable
import com.minbio.erp.corporate_management.models.Links
import com.minbio.erp.corporate_management.models.Meta

data class ProductListModel(
    val `data`: List<ProductListData>,
    val message: String,
    val status: Boolean,
    val links: Links,
    val meta: Meta
)

data class ProductListData(
    val id: Int,
    val image_path: String,
    val name: String,
    val varieties: List<Variety>
)

data class Variety(
    val id: Int,
    val lots: List<Lot>,
    val product_variety: String
)

data class Lot(
    val buying_price: String,
    val `class`: String,
    val discount: String,
    val id: Int,
    val incoming_date: String,
    val product_name: String,
    val lot_no: String,
    val origin: String,
    val product_user_id: Int, /*Variety ID*/
    val product_variety: String,
    val selling_packing_unit: String,
    val income_packing_quantity: String, /*Sale Case*/
    val selling_price: String,
    val selling_tare: String,
    val selling_unit: String,
    val size: String,
    val stock: String,
    val vat: String
)