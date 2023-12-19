package com.minbio.erp.financial_management.accounting.fragments.reporting.purchase_turn_over_invoiced.models

data class AllSuppliersModel(
    var `data`: List<AllSuppliersData>?,
    var message: String?,
    var status: Boolean?
)

data class AllSuppliersData(
    var company_name: String?,
    var contact_full_name: String?,
    var id: Int?
)