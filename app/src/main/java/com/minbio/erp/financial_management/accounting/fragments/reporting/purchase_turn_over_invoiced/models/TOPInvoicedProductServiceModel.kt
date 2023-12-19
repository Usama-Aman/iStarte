package com.minbio.erp.financial_management.accounting.fragments.reporting.purchase_turn_over_invoiced.models

data class TOPInvoicedProductServiceModel(
    var `data`: List<TOPInvoicedProductServiceData>?,
    var message: String?,
    var status: Boolean?,
    var total_amount_exc_tax: String?,
    var total_amount_inc_tax: String?,
    var total_quantity: String?
)

data class TOPInvoicedProductServiceData(
    var amount_exc_tax: String?,
    var amount_inc_tax: String?,
    var amount_percentage: String?,
    var product_variety: String?,
    var quantity: String?,
    var quantity_percentage: String?
)