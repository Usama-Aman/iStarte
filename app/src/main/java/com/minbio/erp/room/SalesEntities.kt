package com.minbio.erp.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SalesModel(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val loggedInUserId: String
)


@Entity
data class SalesOrders(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val loggedInUserId: String,
    val customerId: String,
    val customerName: String,
    val customerSiren: String,
    val customerNTVA: String,
    val customerBalance: String,
    val customerOverdraft: String,
    val customerPayStatus: String,
    val customerImage: String
)

@Entity
data class SalesOrderItems(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val loggedInUserId: String,
    val customerId: String,
    val lotId: String,
    val lotNo: String,
    val productName: String,
    val varietyName: String,
    val varietyId: String,
    val categoryName: String,
    val calibreSize: String,
    val origin: String,
    val price: String,
    val tare: String,
    var quantity: String,
    var totalQuantity: String,
    val packaging: String,
    val incomePackingQuantity: String, /*Sale Case*/
    var vat: String,
    var total: String
)