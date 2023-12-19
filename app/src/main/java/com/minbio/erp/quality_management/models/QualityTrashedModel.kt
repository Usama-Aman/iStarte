package com.minbio.erp.quality_management.models


data class QualityTrashedModel(
    val `data`: List<QualityTrashedData>,
    val handlers: List<QualityTrashedHandler>,
    val message: String,
    val status: Boolean,
    val topics: List<QualityTrashedTopic>,
    val trashed_status: List<String>
)

data class QualityTrashedData(
    val id: Int,
    val lot_no: String,
    val supplier: Supplier
)

data class QualityTrashedHandler(
    val first_name: String,
    val id: Int,
    val image: String,
    val image_path: String,
    val last_name: String
)

data class QualityTrashedTopic(
    val id: Int,
    val topic: String,
    val topic_fr: String
)

class Supplier(
)
