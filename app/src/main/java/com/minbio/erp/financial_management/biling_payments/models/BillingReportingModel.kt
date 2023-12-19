package com.minbio.erp.financial_management.biling_payments.models

data class BillingReportingModel(
    val `data`: BillingReportingData,
    val message: String,
    val status: Boolean
)

data class BillingReportingData(
    val pdf_url: String
)

data class MonthYearModel(
    var id: Int = 0,
    var name: String = ""
)