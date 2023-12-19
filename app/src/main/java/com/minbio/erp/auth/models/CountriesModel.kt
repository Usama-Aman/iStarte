package com.minbio.erp.auth.models

data class CountriesModel(
    val data: List<CountriesData>,
    val message: String,
    val status: Boolean
)

data class CountriesData(
    val id: Int,
    val image_path: String,
    val name: String,
    val name_fr: String
)