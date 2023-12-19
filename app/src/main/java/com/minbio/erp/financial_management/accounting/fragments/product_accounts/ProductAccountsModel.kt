package com.minbio.erp.financial_management.accounting.fragments.product_accounts

import com.minbio.erp.corporate_management.models.Links
import com.minbio.erp.corporate_management.models.Meta

data class ProductAccountsModel(
    val `data`: List<ProductAccountsData>,
    val links: Links,
    val message: String,
    val meta: Meta,
    val status: Boolean
)

data class ProductAccountsData(
    var account_number: String,
    var accounting_chart_account_id: Int,
    var chart_account_label: String,
    var id: Int,
    var mode: String,
    var product_variety: String
)
