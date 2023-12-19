package com.minbio.erp.cashier_desk.models

data class CDCreditNoteModel(
    val complaint_status: List<String>,
    val `data`: List<CNOrdersData>,
    val message: String,
    val status: Boolean
)

data class CNOrdersData(
    val amount: String,
    val complaint_no: String,
    val id: Int,
    val order_id: Int,
    val order_no: String
)
