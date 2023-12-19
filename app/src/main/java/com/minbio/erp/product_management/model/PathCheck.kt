package com.minbio.erp.product_management.model

data class PathCheck(
    val id: Int = 0,
    val path: String,
    val isURL: Boolean = false
)