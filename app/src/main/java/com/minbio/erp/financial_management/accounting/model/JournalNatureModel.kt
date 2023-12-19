package com.minbio.erp.financial_management.accounting.model

data class JournalNatureModel(
    val `data`: List<JournalNature>,
    val message: String,
    val status: Boolean
)

data class JournalNature(
    val id: Int,
    val nature: String
)