package com.minbio.erp.product_management.model

import com.minbio.erp.corporate_management.models.Links
import com.minbio.erp.corporate_management.models.Meta

data class ProCustomerComplaintModel(
    val `data`: List<ProCustomerComplaintData>,
    val message: String,
    val status: Boolean,
    val links: Links,
    val meta: Meta
)

data class ProCustomerComplaintData(
    val comment: String,
    val complaint_no: String,
    val date: String,
    val files: List<File>,
    val id: Int,
    val lot_no: String,
    val order_no: String,
    val status: String,
    val supplier_id: String,
    val supplier_name: String,
    val supplier_note: String
)

data class ProCustomerComplaintFile(
    val id: Int,
    val image_path: String
)