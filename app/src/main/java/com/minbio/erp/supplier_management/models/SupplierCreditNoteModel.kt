package com.minbio.erp.supplier_management.models

import com.minbio.erp.corporate_management.models.Links
import com.minbio.erp.corporate_management.models.Meta

data class SupplierCreditNoteModel(
    val `data`: List<SupplierCreditNoteData>,
    val message: String,
    val links: Links,
    val meta: Meta,
    val status: Boolean
)

data class SupplierCreditNoteData(
    val amount: String,
    val complaint_no: String,
    val creditnote_id: String,
    val date: String,
    val id: Int,
    val invoice_no: String,
    val order_no: String,
    val status: String,
    val supplier_id: String,
    val supplier_note: String
)