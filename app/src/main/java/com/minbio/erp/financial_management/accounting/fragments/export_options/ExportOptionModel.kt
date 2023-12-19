package com.minbio.erp.financial_management.accounting.fragments.export_options

data class ExportOptionModel(
    val `data`: ExportOptionData,
    val message: String,
    val status: Boolean
)

data class ExportOptionData(
    val date_format: String,
    val file_format: String,
    val file_name_prefix: String
)