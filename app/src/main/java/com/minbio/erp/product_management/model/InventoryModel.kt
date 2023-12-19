package com.minbio.erp.product_management.model

import com.minbio.erp.corporate_management.models.Links
import com.minbio.erp.corporate_management.models.Meta

data class InventoryModel(
    val `data`: List<InventoryListData>,
    val message: String,
    val links: Links,
    val meta: Meta,
    val status: Boolean
)

data class InventoryListData(
    val `class`: String,
    val quantity_income_unit: String,
    val id: Int,
    val income_packing_quantity: String,
    val income_unit: String,
    val lot_no: String,
    val origin: String,
    val size: String,
    val stock: String,
    val supplier: String,
    val trashed_quantity: String,
    val buying_cost: String
)