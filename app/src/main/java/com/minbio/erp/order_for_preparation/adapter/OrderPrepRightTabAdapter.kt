package com.minbio.erp.order_for_preparation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.minbio.erp.R
import com.minbio.erp.order_for_preparation.models.OrderDetails

class OrderPrepRightDetailAdapter(_orderDetails: MutableList<OrderDetails>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var orderDetails = _orderDetails
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return Items(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_order_prep_right_tab, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return orderDetails.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is Items)
            holder.bind(position)
    }

    inner class Items(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var lotNo: TextView = itemView.findViewById(R.id.lotNo)
        private var product: TextView = itemView.findViewById(R.id.product)
        private var variety: TextView = itemView.findViewById(R.id.variety)
        private var category: TextView = itemView.findViewById(R.id.category)
        private var calibre: TextView = itemView.findViewById(R.id.calibre)
        private var origin: TextView = itemView.findViewById(R.id.origin)
        private var requestedQuatity: TextView = itemView.findViewById(R.id.requestedQuatity)
        private var packaging: TextView = itemView.findViewById(R.id.packaging)
        private var total: TextView = itemView.findViewById(R.id.total)

        fun bind(position: Int) {

            lotNo.text = orderDetails[position].lot_no
            product.text = orderDetails[position].product_name
            variety.text = orderDetails[position].product_variety
            category.text = orderDetails[position].`class`
            calibre.text = orderDetails[position].size
            origin.text = orderDetails[position].origin
            requestedQuatity.text = orderDetails[position].quantity
            packaging.text = orderDetails[position].unit
            total.text = orderDetails[position].total

        }
    }

}