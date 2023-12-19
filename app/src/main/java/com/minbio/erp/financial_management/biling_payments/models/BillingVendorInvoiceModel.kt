package com.minbio.erp.financial_management.biling_payments.models

import com.minbio.erp.corporate_management.models.Links
import com.minbio.erp.corporate_management.models.Meta

data class BillingVendorInvoiceModel(
    val `data`: List<BillingVendorInvoiceData>,
    val links: Links,
    val message: String,
    val meta: Meta,
    val status: Boolean
)

data class BillingVendorInvoiceData(
    val credit: String,
    val date: String,
    val debit: String,
    val id: Int,
    val invoice_no: String,
    val overdue: String,
    val total_payable_amount: String,
    val payment_method: String,
    val supplier_company_name: String,
    val status: String,
    val supplier_id: String,
    val supplier_note: String,
    val total_amount: String
)