package com.minbio.erp.financial_management.biling_payments.models

import com.minbio.erp.corporate_management.models.Links
import com.minbio.erp.corporate_management.models.Meta

data class BillingCustomerInvoiceModel(
    val `data`: List<BillingCustomerInvoiceData>,
    val links: Links,
    val message: String,
    val meta: Meta,
    val status: Boolean
)

data class BillingCustomerInvoiceData(
    val credit: String,
    val customer_id: String,
    val date: String,
    val debit: String,
    val delivery_type: String,
    val id: Int,
    val order_no: String,
    val overdue: String,
    val payment_method: String,
    val status: String,
    val customer_company_name: String,
    val total_payable_amount: String,
    val time: String
)