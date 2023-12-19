package com.minbio.erp.product_management.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.minbio.erp.R
import com.minbio.erp.product_management.fragments.ProductInventoryFragment
import com.minbio.erp.product_management.model.InventoryListData

class ProductInventoryAdapter(
    _productInventory: ProductInventoryFragment,
    _inventoryList: MutableList<InventoryListData?>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val inventoryList = _inventoryList
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return if (viewType == 0) Items(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.activity_product_inventory_row, parent, false)
        ) else
            Progress(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_pagination_progress, parent, false)
            )
    }


    override fun getItemCount(): Int {
        return inventoryList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is Items) holder.bind(position)

    }

    inner class Items(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txt_lot_no: TextView = itemView.findViewById(R.id.txt_lot_no)
        var txt_cateogry: TextView = itemView.findViewById(R.id.txt_cateogry)
        var txt_calibre: TextView = itemView.findViewById(R.id.txt_calibre)
        var txt_origin: TextView = itemView.findViewById(R.id.txt_origin)
        var txt_supplier: TextView = itemView.findViewById(R.id.txt_supplier)
        var txt_icom_1: TextView = itemView.findViewById(R.id.txt_icom_1)
        var txt_icom_2: TextView = itemView.findViewById(R.id.txt_icom_2)
        var txt_avail_qty: TextView = itemView.findViewById(R.id.txt_avail_qty)
        var txt_trash_qty: TextView = itemView.findViewById(R.id.txt_trash_qty)

        fun bind(position: Int) {
            txt_lot_no.setText(inventoryList[position]?.lot_no);
            txt_cateogry.setText(inventoryList[position]?.`class`);
            txt_calibre.setText(inventoryList[position]?.size);
            txt_origin.setText(inventoryList[position]?.origin);
            txt_supplier.setText(inventoryList[position]?.supplier);
            txt_icom_1.setText(inventoryList[position]?.income_packing_quantity.toString())
            txt_icom_2.setText(inventoryList[position]?.quantity_income_unit.toString())
            txt_avail_qty.setText(inventoryList[position]?.stock.toString())
            txt_trash_qty.setText(inventoryList[position]?.trashed_quantity.toString())
        }
    }

    inner class Progress(itemView: View) : RecyclerView.ViewHolder(itemView)

}