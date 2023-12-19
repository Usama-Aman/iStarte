package com.minbio.erp.financial_management.accounting.fragments.reporting.purchase_turn_over_collected.models

data class TOPCollectedThirdPartyModel(
    var `data`: List<TOPCollectedThirdPartyData>?,
    var message: String?,
    var status: Boolean?,
    var total_amount_inc_tax: String?
)

data class TOPCollectedThirdPartyData(
    var amount_inc_tax: String?,
    var country_name: String?,
    var percentage: String?,
    var supplier_company_name: String?
)