package com.minbio.erp.financial_management.accounting.fragments.personalized_group.models

data class PersonalizedAccountModel(
    val `data`: List<PersonalizedAccountData>,
    val message: String,
    val status: Boolean
)

data class PersonalizedAccountData(
    val account_number: String,
    val id: Int,
    var label: String,
    var isChecked : Boolean = false
)