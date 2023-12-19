package com.minbio.erp.financial_management.accounting.fragments.vendor_invoice_binding.models

import com.minbio.erp.corporate_management.models.Links
import com.minbio.erp.corporate_management.models.Meta

data class VendorInvoiceToBindModel(
    val `data`: List<VendorInvoiceToBindData>,
    val links: Links,
    val message: String,
    val extra_data: ExtraData,
    val meta: Meta,
    val status: Boolean
)

data class VendorInvoiceToBindData(
    val amount: String,
    val comment: String,
    val default_sale_accounting_account_number: String,
    val delivery_note_file_path: String,
    val id: Int,
    val incoming_date: String,
    val invoice_no: String,
    val label_file_path: String,
    val product_variety: String,
    val supplier_country_name: String,
    val supplier_name: String,
    val supplier_note: String,
    val supplier_vat: String,
    var bind_account: String = "",
    var bind_account_id: Int = 0
)

data class ExtraData(
    val account_id_for_sold_products: Int,
    val account_label_for_sold_products: Any,
    val account_number_for_sold_products: Any
)