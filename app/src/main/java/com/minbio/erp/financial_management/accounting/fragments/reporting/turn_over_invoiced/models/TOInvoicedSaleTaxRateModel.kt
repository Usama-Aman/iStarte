package com.minbio.erp.financial_management.accounting.fragments.reporting.turn_over_invoiced.models

data class TOInvoicedSaleTaxRateModel(
    var `data`: TOInvoicedSaleTaxRateData?,
    var message: String?,
    var status: Boolean?
)

data class TOInvoicedSaleTaxRateData(
    var purchase: List<TOInvoicedSaleTaxRate>?,
    var purchase_amount_by_months: TOInvoicedSaleTaxRateReport?,
    var sale: List<TOInvoicedSaleTaxRate>?,
    var sale_amount_by_months: TOInvoicedSaleTaxRateReport?,
    var sale_end_total: String?,
    var purchase_end_total: String?
    )


data class TOInvoicedSaleTaxRate(
    var country_name: String?,
    var end_total: String?,
    var rate: String?,
    var report: TOInvoicedSaleTaxRateReport?,
    var type: String?
)

data class TOInvoicedSaleTaxRateReport(
    var April: String?,
    var August: String?,
    var December: String?,
    var February: String?,
    var January: String?,
    var July: String?,
    var June: String?,
    var March: String?,
    var May: String?,
    var November: String?,
    var October: String?,
    var September: String?
)