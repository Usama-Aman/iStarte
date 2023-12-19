package com.minbio.erp.financial_management.bank_cash.fragments.financial.models

import com.minbio.erp.corporate_management.models.Links
import com.minbio.erp.corporate_management.models.Meta

data class BankCashEntriesModel(
    val `data`: List<BankCashEntriesData>,
    val message: String,
    val links: Links,
    val meta: Meta,
    val status: Boolean
)

data class BankCashEntriesData(
    val account: BankCashEntriesAccount,
    val balance: String,
    val credit: String,
    val date: String,
    val debit: String,
    val description: String,
    val id: Int,
    val number: String,
    val payment_type: String,
    val value_date: String
)

data class BankCashEntriesAccount(
    val account_no: String,
    val bic_swift_code: String,
    val currency: String,
    val iban: String,
    val label: String,
    val reference: String,
    val status: String
)