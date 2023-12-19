package com.minbio.erp.corporate_management.models

data class CorporateCompanyDetailsModel(
    val data: CorporateData,
    val message: String,
    val status: Boolean
)

data class CorporateData(
    val company_address: String,
    val company_name: String,
    val contact_first_name: String,
    val contact_last_name: String,
    val country_code: String,
    val country_id: Int,
    val email: String,
    val id: Int,
    val image_path: String,
    val is_active: Int,
    val iso2: String,
    val phone: String,
    val siret_no: String,
    val zip_code: String,
    val contact_title: String
)