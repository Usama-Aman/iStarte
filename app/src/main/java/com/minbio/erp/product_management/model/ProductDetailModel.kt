package com.minbio.erp.product_management.model

data class ProductDetailModel(
    val `data`: ProductData,
    val message: String,
    val status: Boolean
)

data class ProductData(
    val lot_no: String,
    val invoice_no: String,
    val incoming_date: String,
    val buying_price: String,
    val class_id: Int,
    val comment: String,
    val country_id: Int,
    val gross_weight: String,
    val id: Int,
    val income_packing_quantity: String,
    val income_unit_id: Int,
    val interfel_tax: String,
    val is_bio: Int,
    val is_nego: Int,
    val sale_account_id: Int,
    val purchase_account_id: Int,
    val other_cost: String,
    val product_id: Int,
    val product_variety: String,
    val selling_packing_unit_id: Int,
    val selling_price: String,
    val selling_tare: String,
    val selling_unit_id: Int,
    val selling_vat_id: Int,
    val shipping_cost: String,
    val size_id: Int,
    val stock: String,
    val supplier_id: Int,
    val supplier_note: String,
    val supplier_tare: String,
    val supplier_vat: String,
    val delivery_note_file_path: String,
    val label_file_path: String,
    val files: List<File>

)

data class File(
    val id: Int,
    val image_path: String
)