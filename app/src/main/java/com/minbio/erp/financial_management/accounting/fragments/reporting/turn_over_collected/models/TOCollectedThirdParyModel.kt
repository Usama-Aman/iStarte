package com.minbio.erp.financial_management.accounting.fragments.reporting.turn_over_collected.models

data class TOCollectedThirdPartyModel(
    var `data`: List<TOCollectedThirdPartyData>?,
    var message: String?,
    var status: Boolean?,
    var total_amount_inc_tax: String?
)

data class TOCollectedThirdPartyData(
    var amount_inc_tax: String?,
    var country_name: String?,
    var customer_name: String?,
    var percentage: String?
)