package com.minbio.erp.financial_management.accounting.fragments.registration_in_accounting.models

import com.minbio.erp.corporate_management.models.Links
import com.minbio.erp.corporate_management.models.Meta

data class JournalsModel(
    val `data`: List<JournalsData>,
    val message: String,
    val status: Boolean
)

data class JournalsData(
    val chart_account: String,
    val credit: String,
    val date: String,
    val debit: String,
    val id: Int,
    val label_operation: String,
    val subledger_chart_account: String,
    val payments_type: String
)