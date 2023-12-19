package com.minbio.erp.financial_management.accounting.fragments.reporting.purchase_turn_over_invoiced.models

data class TOPInvoiceThirdPartyModel(
    var `data`: List<TOPInvoiceThirdPartyData>?,
    var message: String?,
    var status: Boolean?,
    var total_amount_exc_tax: String?,
    var total_amount_inc_tax: String?
)

data class TOPInvoiceThirdPartyData(
    var amount_exc_tax: String?,
    var amount_inc_tax: String?,
    var country_name: String?,
    var percentage: String?,
    var supplier_company_name: String?
)