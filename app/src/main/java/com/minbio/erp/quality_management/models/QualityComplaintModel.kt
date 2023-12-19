package com.minbio.erp.quality_management.models

data class QualityComplaintModel(
    val complaint_status: List<String>,
    val `data`: List<QualityComplaintData>,
    val message: String,
    val status: Boolean,
    val types: List<QualityComplaintType>
)

data class QualityComplaintData(
    val id: Int,
    val lot_no: String,
    val supplier: QualityComplaintSupplier
)

data class QualityComplaintType(
    val id: Int,
    val value: String
)

data class QualityComplaintSupplier(
    val id: Int,
    val contact_full_name: String,
    val country_code: String,
    val email: String,
    val phone: String,
    val company_name: String
)