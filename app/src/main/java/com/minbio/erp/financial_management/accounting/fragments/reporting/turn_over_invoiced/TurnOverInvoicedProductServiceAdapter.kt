package com.minbio.erp.financial_management.accounting.fragments.reporting.turn_over_invoiced

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.minbio.erp.R
import com.minbio.erp.financial_management.accounting.fragments.reporting.turn_over_invoiced.models.TOInvoicedProductServiceData

class TurnOverInvoicedProductServiceAdapter(_toInvoicedProductServiceData: MutableList<TOInvoicedProductServiceData?>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var context: Context
    private var toInvoicedProductServiceData = _toInvoicedProductServiceData

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return Items(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_fa_turnover_invoiced_product_service, parent, false)
        )
    }

    override fun getItemCount(): Int = toInvoicedProductServiceData.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is Items)
            holder.bind(position)
    }

    inner class Items(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var product: TextView = itemView.findViewById(R.id.product)
        private var quantity: TextView = itemView.findViewById(R.id.quantity)
        private var amountExcl: TextView = itemView.findViewById(R.id.amountExcl)
        private var amountIncl: TextView = itemView.findViewById(R.id.amountIncl)
        private var percentage: TextView = itemView.findViewById(R.id.percentage)

        @SuppressLint("SetTextI18n")
        fun bind(position: Int) {
            product.text = toInvoicedProductServiceData[position]?.product_variety
            quantity.text = toInvoicedProductServiceData[position]?.quantity
            amountExcl.text = toInvoicedProductServiceData[position]?.amount_exc_tax
            amountIncl.text = toInvoicedProductServiceData[position]?.amount_inc_tax
            percentage.text = "${toInvoicedProductServiceData[position]?.quantity_percentage}%"

        }
    }

}