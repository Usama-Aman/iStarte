package com.minbio.erp.financial_management.accounting.fragments.reporting.purchase_turn_over_invoiced.models

data class TOPInvoicedModel(
    var `data`: List<TOPInvoicedData>?,
    var message: String?,
    var status: Boolean?
)

data class TOPInvoicedData(
    var April: TOPInvoicedReporting?,
    var August: TOPInvoicedReporting?,
    var December: TOPInvoicedReporting?,
    var February: TOPInvoicedReporting?,
    var January: TOPInvoicedReporting?,
    var July: TOPInvoicedReporting?,
    var June: TOPInvoicedReporting?,
    var March: TOPInvoicedReporting?,
    var May: TOPInvoicedReporting?,
    var November: TOPInvoicedReporting?,
    var October: TOPInvoicedReporting?,
    var September: TOPInvoicedReporting?,
    var amount_exc_tax_total: String?,
    var amount_inc_tax_total: String?,
    var year: String?
)

data class TOPInvoicedReporting(
    var amount_exc_tax: String?,
    var amount_inc_tax: String?
)
