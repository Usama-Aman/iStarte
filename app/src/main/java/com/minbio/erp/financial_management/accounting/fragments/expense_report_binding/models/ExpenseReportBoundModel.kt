package com.minbio.erp.financial_management.accounting.fragments.expense_report_binding.models

import com.minbio.erp.corporate_management.models.Links
import com.minbio.erp.corporate_management.models.Meta

data class ExpenseReportBoundModel(
    val `data`: List<ExpenseReportBoundData>,
    val links: Links,
    val message: String,
    val meta: Meta,
    val status: Boolean
)

data class ExpenseReportBoundData(
    val amount: String,
    val chart_account_id: Int,
    val chart_account_label: String,
    val chart_account_number: String,
    val date: String,
    val expense_report_account_label: String,
    val expense_report_account_id: Int,
    val id: Int,
    val note: String,
    val vat_percentage: String,
    var isChecked: Boolean = false
)