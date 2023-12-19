package com.minbio.erp.product_management.model

import com.minbio.erp.corporate_management.models.Links
import com.minbio.erp.corporate_management.models.Meta

data class ProSupplierComplaintModel(
    val `data`: List<ProSupplierComplaintData>,
    val message: String,
    val status: Boolean,
    val links: Links,
    val meta: Meta
)

data class ProSupplierComplaintData(
    val comment: String,
    val complaint_no: String,
    val customer_name: String,
    val date: String,
    val files: List<ProSupplierComplaintFile>,
    val id: Int,
    val order_no: String,
    val status: String
)

data class ProSupplierComplaintFile(
    val id: Int,
    val image_path: String
)