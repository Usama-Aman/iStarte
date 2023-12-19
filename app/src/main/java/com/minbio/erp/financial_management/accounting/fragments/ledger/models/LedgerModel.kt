package com.minbio.erp.financial_management.accounting.fragments.ledger.models

import com.minbio.erp.corporate_management.models.Links
import com.minbio.erp.corporate_management.models.Meta

data class LedgerModel(
    val `data`: List<LedgerData>,
    val links: Links,
    val meta: Meta,
    val message: String,
    val status: Boolean,
    val total_debit: String,
    val total_credit: String
)

data class LedgerData(
    val chart_account_number: String,
    val credit: String,
    val date: String,
    val debit: String,
    val description: String,
    val exported_date: String,
    val id: Int,
    val journal: String,
    val label: String,
    val sub_ledger_account: String,
    val transaction_type: String
)