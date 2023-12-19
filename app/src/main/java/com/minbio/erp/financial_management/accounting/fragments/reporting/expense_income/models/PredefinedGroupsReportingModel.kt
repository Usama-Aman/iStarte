package com.minbio.erp.financial_management.accounting.fragments.reporting.expense_income.models

data class PredefinedGroupsReportingModel(
    var `data`: List<PredefinedGroupsReportingData>?,
    var message: String?,
    var profit: String?,
    var status: Boolean?
)

data class PredefinedGroupsReportingData(
    var account_group: String?,
    var accounts: List<PredefinedGroupsReporting>?,
    var total: String?
)

data class PredefinedGroupsReporting(
    var account_number: String?,
    var amount: String?,
    var label: String?
)