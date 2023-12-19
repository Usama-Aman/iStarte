package com.minbio.erp.financial_management.model

data class ParentAccountsModel(
    val `data`: List<ParentAccountsData>,
    val message: String,
    val status: Boolean
)

data class ParentAccountsData(
    val account_number: String,
    val id: Int,
    val label: String
)