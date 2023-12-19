package com.minbio.erp.financial_management.accounting.fragments.chart_accounts.models

data class PersonalizedGroupModel(
    val `data`: List<PersonalizedGroupData>,
    val message: String,
    val status: Boolean
)

data class PersonalizedGroupData(
    val code: String,
    val id: Int,
    val label: String
)