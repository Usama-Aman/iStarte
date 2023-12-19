package com.minbio.erp.human_resources.models

data class VatsModel(
    val `data`: List<VatsData>,
    val message: String,
    val status: Boolean
)

data class VatsData(
    val code: String,
    val id: Int,
    val note: String,
    val rate: String
)