package com.minbio.erp.accounting.models

data class AccountingCreditNoteModel(
    val complaint_status: List<String>,
    val `data`: List<AccountingCreditNoteData>,
    val message: String,
    val status: Boolean
)

data class AccountingCreditNoteData(
    val amount: String,
    val complaint_no: String,
    val delivery_note: String,
    val id: Int,
    val invoice_no: String,
    val supplier_email: String,
    val date: String
)