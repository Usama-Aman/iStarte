package com.minbio.erp.financial_management.accounting.fragments.expense_report_binding.models

data class ExpenseReportBindingModel(
    val `data`: ExpenseReportBindingData,
    val message: String,
    val status: Boolean
)

data class ExpenseReportBindingData(
    val bound: List<ExpenseReportBinding>,
    val tobind: ExpenseReportBinding
)

data class ExpenseReportBinding(
    val account_number: String,
    val report: ExpenseReportBindingReport,
    val total_amount: String,
    val label: String
)

data class ExpenseReportBindingReport(
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