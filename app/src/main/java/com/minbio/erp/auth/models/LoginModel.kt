package com.minbio.erp.auth.models

data class LoginModel(
    val access_token: String,
    val data: LoginData,
    val message: String,
    val status: Boolean
)

data class LoginData(
    val company_name: String,
    val company_id: Int,
    val country_code: String,
    val designation: String,
    val designation_id: Int,
    val email: String,
    val email_verified: Int,
    val first_name: String,
    val id: Int,
    val image_path: String,
    val is_active: Int,
    val iso2: String,
    val lang: String,
    val last_name: String,
    val permissions: Permissions,
    val phone: String,
    val phone_verified: Int,
    val company_image_path: String,
    val designation_key: String
)

data class Permissions(
    val accounting: String,
    val cashier_desk: String,
    val corporate_management: String,
    val customer_management: String,
    val order_preparation: String,
    val product_management: String,
    val sales_management: String,
    val quality_management: String,
    val hr_management: String,
    val supplier_management: String
)