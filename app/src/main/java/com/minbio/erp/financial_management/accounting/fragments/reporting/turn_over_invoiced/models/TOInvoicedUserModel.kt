package com.minbio.erp.financial_management.accounting.fragments.reporting.turn_over_invoiced.models

data class TOInvoicedUserModel(
    var `data`: List<TOInvoicedUserData>?,
    var message: String?,
    var status: Boolean?,
    var total_amount_exc_tax: String?,
    var total_amount_inc_tax: String?
)

data class TOInvoicedUserData(
    var amount_exc_tax: String?,
    var amount_inc_tax: String?,
    var percentage: String?,
    var user_name: String?
)