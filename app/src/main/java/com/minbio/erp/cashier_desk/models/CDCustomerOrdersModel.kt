package com.minbio.erp.cashier_desk.models

import com.minbio.erp.corporate_management.models.Links
import com.minbio.erp.corporate_management.models.Meta

data class CDCustomerOrdersModel(
    val `data`: List<CDCustomersData>,
    val links: Links,
    val message: String,
    val meta: Meta,
    val status: Boolean
)

data class CDCustomersData(
    val full_name: String,
    val id: Int,
    val image_path: String,
    val pending_orders: List<CDPendingOrder>
)

data class CDPendingOrder(
    val date: String,
    val details: CDOrderDetail,
    val due_date: String,
    val id: Int,
    val order_no: String,
    val status: String,
    val time: String
)
