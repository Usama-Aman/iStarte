package com.minbio.erp.financial_management.biling_payments.models

import com.minbio.erp.corporate_management.models.Links
import com.minbio.erp.corporate_management.models.Meta

data class BillingVendorPaymentModel(
    val `data`: List<BillingVendorPaymentData>,
    val links: Links,
    val message: String,
    val meta: Meta,
    val status: Boolean
)

data class BillingVendorPaymentData(
    val date: String,
    val id: Int,
    val invoice_no: String,
    val payment_method: String,
    val supplier_company_name: String,
    val total_payable_amount: String
)
