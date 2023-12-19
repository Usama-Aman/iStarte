package com.minbio.erp.financial_management.accounting.fragments.reporting.turn_over_invoiced

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.minbio.erp.R
import com.minbio.erp.financial_management.accounting.fragments.reporting.turn_over_invoiced.models.TOInvoicedSaleTaxRate

class TurnOverInvoicedSaleTaxAdapter(_taxRate: MutableList<TOInvoicedSaleTaxRate>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var context: Context
    private var taxRate = _taxRate

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return Items(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_fa_turnover_invoiced_sale_tax_rate, parent, false)
        )
    }

    override fun getItemCount(): Int = taxRate.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is Items)
            holder.bind(position)
    }

    inner class Items(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var turnOver: TextView = itemView.findViewById(R.id.turnOver)
        private var product: TextView = itemView.findViewById(R.id.product)
        private var country: TextView = itemView.findViewById(R.id.country)
        private var Jan: TextView = itemView.findViewById(R.id.Jan)
        private var Feb: TextView = itemView.findViewById(R.id.Feb)
        private var Mar: TextView = itemView.findViewById(R.id.Mar)
        private var Apr: TextView = itemView.findViewById(R.id.Apr)
        private var May: TextView = itemView.findViewById(R.id.May)
        private var Jun: TextView = itemView.findViewById(R.id.Jun)
        private var Jul: TextView = itemView.findViewById(R.id.Jul)
        private var Aug: TextView = itemView.findViewById(R.id.Aug)
        private var Sep: TextView = itemView.findViewById(R.id.Sep)
        private var Oct: TextView = itemView.findViewById(R.id.Oct)
        private var Nov: TextView = itemView.findViewById(R.id.Nov)
        private var Dec: TextView = itemView.findViewById(R.id.Dec)
        private var total: TextView = itemView.findViewById(R.id.total)

        fun bind(position: Int) {

            turnOver.text = taxRate[position].rate
            product.text = taxRate[position].type
            country.text = taxRate[position].country_name
            Jan.text = taxRate[position].report?.January
            Feb.text = taxRate[position].report?.February
            Mar.text = taxRate[position].report?.March
            Apr.text = taxRate[position].report?.April
            May.text = taxRate[position].report?.May
            Jun.text = taxRate[position].report?.June
            Jul.text = taxRate[position].report?.July
            Aug.text = taxRate[position].report?.August
            Sep.text = taxRate[position].report?.September
            Oct.text = taxRate[position].report?.October
            Nov.text = taxRate[position].report?.November
            Dec.text = taxRate[position].report?.December
            total.text = taxRate[position].end_total

        }
    }

}