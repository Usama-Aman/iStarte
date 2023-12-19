package com.minbio.erp.financial_management.biling_payments.models

data class BillingStatisticsModel(
    val `data`: List<BillingStatisticsData>,
    val message: String,
    val status: Boolean
)

data class BillingStatisticsData(
    val average_amount_per_year: String,
    val monthly_stats: MonthlyStats,
    val orders_per_year: Int,
    val total_amount_per_year: Double,
    val year: Any
)

data class MonthlyStats(
    val Apr: Stats,
    val Aug: Stats,
    val Dec: Stats,
    val Feb: Stats,
    val Jan: Stats,
    val Jul: Stats,
    val Jun: Stats,
    val Mar: Stats,
    val May: Stats,
    val Nov: Stats,
    val Oct: Stats,
    val Sep: Stats
)

data class Stats(
    var average_amount: String = "0",
    var total_amount: String = "0",
    val total_orders: Int = 0
)