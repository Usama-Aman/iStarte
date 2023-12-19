package com.minbio.erp.order_for_preparation.models

import com.minbio.erp.corporate_management.models.Links
import com.minbio.erp.corporate_management.models.Meta

data class CustomerOrdersModel(
    val `data`: List<CustomerOrderData>,
    val links: Links,
    val message: String,
    val meta: Meta,
    val status: Boolean
)

data class CustomerOrderData(
    val full_name: String,
    val image_path: String,
    val id: Int,
    val pending_orders: List<PendingOrder>
)

data class PendingOrder(
    val date: String,
    val details: List<OrderDetails>,
    val id: Int,
    val order_no: String,
    val status: String,
    val time: String
)

data class OrderDetails(
    val `class`: String,
    val discount: String,
    val id: Int,
    val lot_no: String,
    val origin: String,
    val price: String,
    val product_name: String,
    val product_variety: String,
    val quantity: String,
    val size: String,
    val total: String,
    val unit: String,
    val vat: String
)

data class OPRightTabs(
    val pendingListId: Int,
    val order_no: String,
    val details: List<OrderDetails>
)

data class PendingOrderModel(
    val `data`: List<PendingOrderData>,
    val links: Links,
    val message: String,
    val meta: Meta,
    val status: Boolean
)

data class PendingOrderData(
    val date: String,
    val time: String,
    val details: List<OrderDetails>,
    val id: Int,
    val order_no: String,
    val status: String
)