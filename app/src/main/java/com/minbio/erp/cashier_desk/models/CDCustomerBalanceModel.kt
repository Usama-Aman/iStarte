package com.minbio.erp.cashier_desk.models

import com.minbio.erp.corporate_management.models.Links
import com.minbio.erp.corporate_management.models.Meta

data class CDCustomerBalanceModel(
    val `data`: List<CDBalanceData>,
    val links: Links,
    val message: String,
    val meta: Meta,
    val status: Boolean
)

data class CDBalanceData(
    val credit: String,
    val customer_id: String,
    val date: String,
    val debit: String,
    val id: Int,
    val order_no: String,
    val overdue: String,
    val payment_method: String,
    val status: String,
    val delivery_type: String,
    val time: String,
    val total_amount: String
)
