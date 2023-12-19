package com.minbio.erp.supplier_management.models

import com.minbio.erp.corporate_management.models.Links
import com.minbio.erp.corporate_management.models.Meta

data class SupplierBalanceModel(
    val `data`: List<SupplierBalanceData>,
    val message: String,
    val links: Links,
    val meta: Meta,
    val status: Boolean
)

data class SupplierBalanceData(
    val credit: String,
    val date: String,
    val debit: String,
    val id: Int,
    val invoice_no: String,
    val overdue: String,
    val payment_method: String,
    val status: String,
    val supplier_id: String,
    val supplier_note: String,
    val total_amount: String
)