package com.minbio.erp.financial_management.accounting.fragments.ledger.models

data class NewTransactionModel(
    val `data`: NewTransactionData,
    val message: String,
    val status: Boolean
)

data class NewTransactionData(
    val transaction_id: String,
    val transaction_type: String,
    val journal: Int,
    val created_at: String,
    val accounting_doc: String,
    val date: String,
    val movements: List<NewTransactionMovements>
)

data class NewTransactionMovements(
    var id : Int,
    var chart_account_number: String,
    var chart_account_label: String,
    var chart_account_id: Int,
    var sub_ledger_account: String,
    var label: String,
    var debit: String,
    var credit: String,
    var isDebitCreditBothEdit: Boolean = false
)