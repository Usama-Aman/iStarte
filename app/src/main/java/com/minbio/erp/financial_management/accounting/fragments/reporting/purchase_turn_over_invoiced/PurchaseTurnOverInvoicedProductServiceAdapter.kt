package com.minbio.erp.financial_management.accounting.fragments.reporting.purchase_turn_over_invoiced

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.minbio.erp.R
import com.minbio.erp.financial_management.accounting.fragments.reporting.purchase_turn_over_invoiced.models.TOPInvoicedProductServiceData

class PurchaseTurnOverInvoicedProductServiceAdapter(_topInvoicedProductServiceData: MutableList<TOPInvoicedProductServiceData?>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var context: Context
    private var topInvoicedProductServiceData = _topInvoicedProductServiceData

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return Items(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_fa_turnover_invoiced_product_service, parent, false)
        )
    }

    override fun getItemCount(): Int = topInvoicedProductServiceData.size

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
            product.text = topInvoicedProductServiceData[position]?.product_variety
            quantity.text = topInvoicedProductServiceData[position]?.quantity
            amountExcl.text = topInvoicedProductServiceData[position]?.amount_inc_tax
            amountIncl.text = topInvoicedProductServiceData[position]?.amount_exc_tax
            percentage.text = "${topInvoicedProductServiceData[position]?.quantity_percentage}%"
        }
    }

}