package com.minbio.erp.financial_management.accounting.fragments.ledger.models

import com.minbio.erp.corporate_management.models.Links
import com.minbio.erp.corporate_management.models.Meta

data class LedgerGroupByAccountsModel(
    val `data`: List<LedgerGroupByAccountsData>,
    val status: String,
    val message: String,
    val links: Links,
    val meta: Meta
)

data class LedgerGroupByAccountsData(
    val account_number: String,
    val chart_account_label: String,
    val id: Int,
    val ledger_entries: List<LedgerGroupByAccountsEntry>,
    val total_credit: String,
    val total_debit: String,
    val end_total_credit: String,
    val end_total_debit: String,
    val credit_balance: String,
    val debit_balance: String
)

data class LedgerGroupByAccountsEntry(
    val credit: String,
    val date: String,
    val debit: String,
    val description: String,
    val id: Int,
    val journal: String,
    val label: String,
    val transaction_id: Long
)