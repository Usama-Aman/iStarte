package com.minbio.erp.financial_management.accounting.fragments.customer_invoice_binding.models

import com.minbio.erp.corporate_management.models.Links
import com.minbio.erp.corporate_management.models.Meta

data class CustomerInvoiceToBindModel(
    val `data`: List<CustomerInvoiceToBindData>,
    val message: String,
    val extra_data: ExtraData,
    val links: Links,
    val meta: Meta,
    val status: Boolean
)

data class CustomerInvoiceToBindData(
    val amount: String,
    var chart_account_id: Int,
    val customer_country_name: String,
    val customer_name: String,
    val date: String,
    val desc: String,
    val id: Int,
    val invoice: String,
    val product_chart_account_id: Int,
    val product_chart_account_number: String,
    val product_country_name: String,
    val product_variety: String,
    val vat: String,
    var bind_account: String = ""
)

data class ExtraData(
    val account_id_for_sold_products: Int,
    val account_label_for_sold_products: Any,
    val account_number_for_sold_products: Any
)