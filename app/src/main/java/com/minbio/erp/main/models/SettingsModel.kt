package com.minbio.erp.main.models

data class SettingsModel(
    val `data`: SettingsData,
    val message: String,
    val status: Boolean
)

data class SettingsData(
    val date_formats: List<DateFormat>,
    val settings: Settings
)

data class DateFormat(
    val format: String,
    val value: Int
)

data class Settings(
    val date_format: String,
    val default_currency: String
)