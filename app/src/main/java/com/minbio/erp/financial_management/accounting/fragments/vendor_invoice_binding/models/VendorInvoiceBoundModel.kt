package com.minbio.erp.financial_management.accounting.fragments.vendor_invoice_binding.models

import com.minbio.erp.corporate_management.models.Links
import com.minbio.erp.corporate_management.models.Meta

data class VendorInvoiceBoundModel(
    val `data`: List<VendorInvoiceBoundData>,
    val links: Links,
    val message: String,
    val meta: Meta,
    val status: Boolean
)

data class VendorInvoiceBoundData(
    val amount: String,
    val chart_account_id: Int,
    val chart_account_label: String,
    val chart_account_number: String,
    val comment: String,
    val default_sale_accounting_account_number: Any,
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
    var isChecked : Boolean = false
)
