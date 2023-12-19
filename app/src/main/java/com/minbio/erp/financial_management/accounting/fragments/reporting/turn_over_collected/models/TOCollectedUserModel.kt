package com.minbio.erp.financial_management.accounting.fragments.reporting.turn_over_collected.models

data class TOCollectedUserModel(
    var `data`: List<TOCollectedUserData>?,
    var message: String?,
    var status: Boolean?,
    var total_amount_inc_tax: String?
)

data class TOCollectedUserData(
    var amount_inc_tax: String?,
    var percentage: String?,
    var user_name: String?
)