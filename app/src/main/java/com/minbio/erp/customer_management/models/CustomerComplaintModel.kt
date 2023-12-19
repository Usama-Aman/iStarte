package com.minbio.erp.customer_management.models

data class CustomerComplaintModel(
    val `data`: ComplaintData,
    val message: String,
    val status: Boolean
)

data class ComplaintData(
    val orders: List<ComplaintOrders>,
val status: List<String>,
    val types: List<ComplaintTypes>
)

data class ComplaintOrders(
    val id: Int,
    val items: List<ComplaintItems>,
    val value: String
)

data class ComplaintTypes(
    val id: Int,
    val value: String
)

data class ComplaintItems(
    val id: Int,
    val value: String,
    var isChecked: Boolean
)