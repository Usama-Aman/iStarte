package com.minbio.erp.supplier_management.models

import com.minbio.erp.corporate_management.models.Links
import com.minbio.erp.corporate_management.models.Meta

data class SupplierComplaintModel(
    val `data`: List<SupplierComplaintData>,
    val message: String,
    val status: Boolean,
    val links: Links,
    val meta: Meta
)

data class SupplierComplaintData(
    val complaint_no: String,
    val date: String,
    val id: Int,
    val order_no: String,
    val status: String,
    val supplier_id: String,
    val supplier_note: String
)