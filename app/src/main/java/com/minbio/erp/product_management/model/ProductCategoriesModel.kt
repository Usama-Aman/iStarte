package com.minbio.erp.product_management.model

data class ProductCategoriesModel(
    val `data`: List<Data>
)

data class Data(
    val `file`: String,
    val id: Int,
    val image_path: String,
    val name: String
)