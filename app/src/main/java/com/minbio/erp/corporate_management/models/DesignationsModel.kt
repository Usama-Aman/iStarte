package com.minbio.erp.corporate_management.models

data class DesignationsModel(
    val `data`: List<DesignationData> = ArrayList(),
    val message: String,
    val status: Boolean
)

data class DesignationData(
    val designation: String,
    val id: Int,
    val permissions: List<Permission>
)

data class Permission(
    val description: String,
    val id: Int,
    val permission: String,
    val pivot: Pivot
)

data class Pivot(
    val created_at: String,
    val designation_id: Int,
    val permission_id: Int
)