package com.minbio.erp.financial_management.accounting.fragments.default_accounts

data class DefaultAccountModel(
    val `data`: DefaultAccountData,
    val message: String,
    val status: Boolean
)

data class DefaultAccountData(
    val account_capital: Int,
    val account_for_bought_products: Int,
    val account_for_bought_products_and_imported_out_of_ecc: Int,
    val account_for_bought_services: Int,
    val account_for_bought_services_and_imported_out_of_ecc: Int,
    val account_for_customer_third_parties: Int,
    val account_for_paying_vat: Int,
    val account_for_products_sold_and_exported_out_of_eec: Int,
    val account_for_services_sold_and_exported_out_of_ecc: Int,
    val account_for_sold_products: Int,
    val account_for_sold_services: Int,
    val account_for_user_third_parties: Int,
    val account_for_vat_on_purchases: Int,
    val account_for_vat_on_sales: Int,
    val account_for_vendor_third_parties: Int,
    val account_insurance: Int,
    val account_interest: Int,
    val account_of_transitional_bank_transfer: Int,
    val account_of_wait: Int,
    val account_to_register_donations: Int
)