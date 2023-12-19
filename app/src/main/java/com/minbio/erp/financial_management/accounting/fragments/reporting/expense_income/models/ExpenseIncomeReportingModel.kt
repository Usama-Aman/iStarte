package com.minbio.erp.financial_management.accounting.fragments.reporting.expense_income.models

data class ExpenseIncomeReportingModel(
    var `data`: List<ExpenseIncomeReportingData>?,
    var message: String?,
    var status: Boolean?
)

data class ExpenseIncomeReportingData(
    var April: ExpenseIncomeReporting?,
    var August: ExpenseIncomeReporting?,
    var December: ExpenseIncomeReporting?,
    var February: ExpenseIncomeReporting?,
    var January: ExpenseIncomeReporting?,
    var July: ExpenseIncomeReporting?,
    var June: ExpenseIncomeReporting?,
    var March: ExpenseIncomeReporting?,
    var May: ExpenseIncomeReporting?,
    var November: ExpenseIncomeReporting?,
    var October: ExpenseIncomeReporting?,
    var September: ExpenseIncomeReporting?,
    var accounting_result: String?,
    var expense_total: String?,
    var income_total: String?,
    var year: String?
)

data class ExpenseIncomeReporting(
    var expense: String?,
    var income: String?
)