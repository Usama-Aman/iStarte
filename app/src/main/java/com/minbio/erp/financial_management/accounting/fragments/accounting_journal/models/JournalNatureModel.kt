package com.minbio.erp.financial_management.accounting.fragments.accounting_journal.models

data class JournalNatureModel(
    val `data`: List<JournalNatureData>,
    val message: String,
    val status: Boolean
)

data class JournalNatureData(
    val id: Int,
    val nature: String
)