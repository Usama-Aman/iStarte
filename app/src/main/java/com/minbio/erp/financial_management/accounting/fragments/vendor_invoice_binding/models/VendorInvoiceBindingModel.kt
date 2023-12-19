package com.minbio.erp.financial_management.accounting.fragments.vendor_invoice_binding.models

data class VendorInvoiceBindingModel(
    val `data`: VendorInvoiceBindingData,
    val message: String,
    val status: Boolean
)

data class VendorInvoiceBindingData(
    val bound: List<VendorInvoiceBinding>,
    val tobind: VendorInvoiceBinding
)

data class VendorInvoiceBinding(
    val account_number: String,
    val report: CustomerInvoiceBindingReport,
    val total_amount: String,
    val label: String
)

data class CustomerInvoiceBindingReport(
    val Apr: String,
    val Aug: String,
    val Dec: String,
    val Feb: String,
    val Jan: String,
    val Jul: String,
    val Jun: String,
    val Mar: String,
    val May: String,
    val Nov: String,
    val Oct: String,
    val Sep: String
)