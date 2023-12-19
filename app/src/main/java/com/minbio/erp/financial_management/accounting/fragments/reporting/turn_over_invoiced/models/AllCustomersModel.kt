package com.minbio.erp.financial_management.accounting.fragments.reporting.turn_over_invoiced.models

data class AllCustomersModel(
    var `data`: List<AllCustomersData>?,
    var message: String?,
    var status: Boolean?
)

data class AllCustomersData(
    var full_name: String?,
    var id: Int?,
    var image_path: String?
)