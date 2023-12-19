package com.minbio.erp.product_management.model

data class GradientsModel(
    val `data`: GradientData,
    val message: String,
    val status: Boolean
)

data class GradientData(
    val categories: List<Category>,
    val classes: List<Classes>,
    val countries: List<Country>,
    val sizes: List<Size>,
    val units: List<Unit>,
    val vats: List<Vat>
)

data class Category(
    val `file`: String,
    val id: Int,
    val image_path: String,
    val name: String
)

data class Classes(
    val id: Int,
    val name: String,
    val sign: String
)

data class Country(
    val id: Int,
    val image_path: String,
    val name: String,
    val name_fr: String
)

data class Size(
    val id: Int,
    val name: String,
    val sign: String
)

data class Unit(
    val id: Int,
    val name: String,
    val sign: String,
    val subunits: List<Any>
)

data class Vat(
    val id: Int,
    val value: String
)