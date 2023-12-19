package com.minbio.erp.financial_management.biling_payments.models

import com.minbio.erp.corporate_management.models.Links
import com.minbio.erp.corporate_management.models.Meta

data class BillingCustomerPaymentModel(
    val `data`: List<BillingCustomerPaymentData>,
    val links: Links,
    val message: String,
    val meta: Meta,
    val status: Boolean
)

data class BillingCustomerPaymentData(
    val customer_name: String,
    val date: String,
    val id: Int,
    val order_no: String,
    val paid_amount: String,
    val payment_method: String
)