package com.minbio.erp.accounting.models

data class FileTransferModel(
    val `data`: FileTransferData,
    val message: String,
    val status: Boolean
)

data class FileTransferData(
    val customer_list_transfer: String,
    val inventory_transfer: String,
    val invoice_list_transfer: String,
    val order_transfer: String,
    val product_list_transfer: String,
    val supplier_list_transfer: String,
    val third_party_download_upload: String
)