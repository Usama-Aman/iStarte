package com.minbio.erp.financial_management.accounting.fragments.reporting.turn_over_collected.models

data class TOCollectedModel(
    var `data`: List<TOCollectedData>?,
    var message: String?,
    var status: Boolean?
)

data class TOCollectedData(
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
    var September: String?,
    var amount_inc_tax_total: String?,
    var year: String?
)