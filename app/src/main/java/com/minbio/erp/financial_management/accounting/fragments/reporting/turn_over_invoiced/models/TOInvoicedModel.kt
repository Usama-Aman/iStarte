package com.minbio.erp.financial_management.accounting.fragments.reporting.turn_over_invoiced.models

data class TOInvoicedModel(
    var `data`: List<TOInvoicedData>?,
    var message: String?,
    var status: Boolean?
)

data class TOInvoicedData(
    var April: TOInvoicedReporting?,
    var August: TOInvoicedReporting?,
    var December: TOInvoicedReporting?,
    var February: TOInvoicedReporting?,
    var January: TOInvoicedReporting?,
    var July: TOInvoicedReporting?,
    var June: TOInvoicedReporting?,
    var March: TOInvoicedReporting?,
    var May: TOInvoicedReporting?,
    var November: TOInvoicedReporting?,
    var October: TOInvoicedReporting?,
    var September: TOInvoicedReporting?,
    var amount_exc_tax_total: String?,
    var amount_inc_tax_total: String?,
    var year: String?
)

data class TOInvoicedReporting(
    var amount_exc_tax: String?,
    var amount_inc_tax: String?
)