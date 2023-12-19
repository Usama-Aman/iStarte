package com.minbio.erp.financial_management.bank_cash.fragments.financial.models

data class BankCashMetaDataModel(
    val `data`: BankCashMetaData,
    val message: String,
    val status: Boolean
)

data class BankCashMetaData(
    val account_types: List<BankCashAccountTypes>,
    val countries: List<BankCashCountry>,
    val currencies: List<BankCashCurrency>,
    val status: List<String>,
    val payment_types: List<BankCashPayTypes>,
    val accounts: List<BankCashAccount>,
    val types: List<String>
)

data class BankCashCountry(
    val code: String,
    val `file`: String,
    val id: Int,
    val image_path: String,
    val name: String,
    val name_fr: String
)

data class BankCashCurrency(
    val code: String,
    val id: Int,
    val name: String,
    val symbol: String
)

data class BankCashAccountTypes(
    val id: Int,
    val type: String,
    val type_fr: String
)

data class BankCashPayTypes(
    val id: Int,
    val type: String,
    val type_fr: String
)

data class  BankCashAccount(
    val id: Int,
    val label: String
)