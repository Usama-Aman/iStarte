package com.minbio.erp.financial_management.accounting.fragments.customer_invoice_binding.models

data class CustomerInvoiceBindingModel(
    val `data`: CustomerInvoiceBindingData,
    val message: String,
    val status: Boolean
)

data class CustomerInvoiceBindingData(
    val bound: List<CustomerInvoiceBinding>,
    val tobind: CustomerInvoiceBinding
)

data class CustomerInvoiceBinding(
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