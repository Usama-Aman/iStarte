package com.minbio.erp.financial_management.accounting.model

import com.minbio.erp.corporate_management.models.Links
import com.minbio.erp.corporate_management.models.Meta

data class AccountingJournalsModel(
    val `data`: List<AccountingJournals>,
    val links: Links,
    val message: String,
    val meta: Meta,
    val status: Boolean
)

data class AccountingJournals(
    val code: String,
    val id: Int,
    val journal_nature: String,
    val label: String
)