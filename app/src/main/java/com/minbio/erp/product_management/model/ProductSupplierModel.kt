package com.minbio.erp.product_management.model

data class ProductSupplierModel(
    val `data`: ProductSupplierData,
    val message: String,
    val status: Boolean
)

data class ProductSupplierData(
    val products: List<Product>,
    val suppliers: List<Supplier>
)

data class Product(
    val id: Int,
    val name: String,
    val name_fr: String
)

data class Supplier(
    val company_name: String,
    val contact_full_name: String,
    val id: Int
)