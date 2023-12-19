package com.minbio.erp.corporate_management.models

data class BankDetailModel(
    val data: BankData,
    val message: String,
    val status: Boolean
)

data class BankData(
    val bic_code: String,
    val iban: String,
    val bank_detail_photo_path: String = ""
)