package com.minbio.erp.financial_management.accounting.fragments.customer_invoice_binding.models

import com.minbio.erp.corporate_management.models.Links
import com.minbio.erp.corporate_management.models.Meta

data class CustomerInvoiceBoundModel(
    val `data`: List<CustomerInvoiceBoundData>,
    val links: Links,
    val message: String,
    val meta: Meta,
    val status: Boolean
)

data class CustomerInvoiceBoundData(
    val amount: String,
    val chart_account_id: Int,
    val chart_account_label: String,
    val chart_account_number: String,
    val customer_country_name: String,
    val customer_name: String,
    val date: String,
    val desc: String,
    val id: Int,
    val invoice: String,
    val product_chart_account_id: Int,
    val product_country_name: String,
    val product_variety: String,
    val vat: String,
    var isChecked: Boolean = false
)
