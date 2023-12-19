package com.minbio.erp.financial_management.accounting.fragments.reporting.expense_income.models

data class PersonalizedGroupReportingModel(
    var `data`: List<PersonalizedGroupReportingData>?,
    var message: String?,
    var status: Boolean?
)

data class PersonalizedGroupReportingData(
    var group: String?,
    var previous_period_amount: String?,
    var selected_period_amount: String?,
    var label: String?,
    var report: PersonalizedGroupReport?,
    var accounts: List<PersonalizedGroupReportAccounts?>
)

data class PersonalizedGroupReport(
    var April: String?,
    var August: String?,
    var December: String?,
    var February: String?,
    var January: String?,
    var July: String?,
    var June: String?,
    var March: String?,
    var May: String?,
    var November: String?,
    var October: String?,
    var September: String?
)

data class PersonalizedGroupReportAccounts(
    var account_number: String?,
    var label: String?,
    var previous_period_amount: String?,
    var selected_period_amount: String?,
    var report: PersonalizedGroupReport?
)