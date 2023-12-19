package com.minbio.erp.financial_management.accounting.fragments.reporting.purchase_turn_over_invoiced

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.minbio.erp.R
import com.minbio.erp.financial_management.accounting.fragments.reporting.purchase_turn_over_invoiced.models.TOPInvoicedData
import com.minbio.erp.utils.Constants
import com.minbio.erp.utils.SharedPreference

class PurchaseTurnOverInvoicedAdapter(_topInvoicedData: MutableList<TOPInvoicedData>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var context: Context
    private var topInvoicedData = _topInvoicedData

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return Items(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_fa_purchase_turnover_invoiced, parent, false)
        )
    }

    override fun getItemCount(): Int =topInvoicedData.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is Items)
            holder.bind(position)
    }

    inner class Items(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var year: TextView = itemView.findViewById(R.id.year)
        private var janExpense: TextView = itemView.findViewById(R.id.janExpense)
        private var febExpense: TextView = itemView.findViewById(R.id.febExpense)
        private var marExpense: TextView = itemView.findViewById(R.id.marExpense)
        private var aprExpense: TextView = itemView.findViewById(R.id.aprExpense)
        private var mayExpense: TextView = itemView.findViewById(R.id.mayExpense)
        private var junExpense: TextView = itemView.findViewById(R.id.junExpense)
        private var julyExpense: TextView = itemView.findViewById(R.id.julyExpense)
        private var augExpense: TextView = itemView.findViewById(R.id.augExpense)
        private var sepExpense: TextView = itemView.findViewById(R.id.sepExpense)
        private var octExpense: TextView = itemView.findViewById(R.id.octExpense)
        private var novExpense: TextView = itemView.findViewById(R.id.novExpense)
        private var decExpense: TextView = itemView.findViewById(R.id.decExpense)
        private var janIncome: TextView = itemView.findViewById(R.id.janIncome)
        private var febIncome: TextView = itemView.findViewById(R.id.febIncome)
        private var marIncome: TextView = itemView.findViewById(R.id.marIncome)
        private var aprIncome: TextView = itemView.findViewById(R.id.aprIncome)
        private var mayIncome: TextView = itemView.findViewById(R.id.mayIncome)
        private var junIncome: TextView = itemView.findViewById(R.id.junIncome)
        private var julyIncome: TextView = itemView.findViewById(R.id.julyIncome)
        private var augIncome: TextView = itemView.findViewById(R.id.augIncome)
        private var sepIncome: TextView = itemView.findViewById(R.id.sepIncome)
        private var octIncome: TextView = itemView.findViewById(R.id.octIncome)
        private var novIncome: TextView = itemView.findViewById(R.id.novIncome)
        private var decIncome: TextView = itemView.findViewById(R.id.decIncome)
        private var totalAmountExl: TextView = itemView.findViewById(R.id.totalAmount)
        private var totalAmountIncl: TextView = itemView.findViewById(R.id.totalDelta)
        private var faptoitvAmountExcl: TextView = itemView.findViewById(R.id.faptoitvAmountExcl)
        private var faptoitvAmountIncl: TextView = itemView.findViewById(R.id.faptoitvAmountIncl)

        fun bind(position: Int) {
            faptoitvAmountExcl.text = context!!.resources.getString(
                R.string.faptoiLabelAmountExcl,
                SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
            )
            faptoitvAmountIncl.text = context!!.resources.getString(
                R.string.faptoiLabelAmountIncl,
                SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
            )

            year.text = topInvoicedData[position].year
            janExpense.text = topInvoicedData[position].January?.amount_exc_tax
            febExpense.text = topInvoicedData[position].February?.amount_exc_tax
            marExpense.text = topInvoicedData[position].March?.amount_exc_tax
            aprExpense.text = topInvoicedData[position].April?.amount_exc_tax
            mayExpense.text = topInvoicedData[position].May?.amount_exc_tax
            junExpense.text = topInvoicedData[position].June?.amount_exc_tax
            julyExpense.text = topInvoicedData[position].July?.amount_exc_tax
            augExpense.text = topInvoicedData[position].August?.amount_exc_tax
            sepExpense.text = topInvoicedData[position].September?.amount_exc_tax
            octExpense.text = topInvoicedData[position].October?.amount_exc_tax
            novExpense.text = topInvoicedData[position].November?.amount_exc_tax
            decExpense.text = topInvoicedData[position].December?.amount_exc_tax
            janIncome.text = topInvoicedData[position].January?.amount_inc_tax
            febIncome.text = topInvoicedData[position].February?.amount_inc_tax
            marIncome.text = topInvoicedData[position].March?.amount_inc_tax
            aprIncome.text = topInvoicedData[position].April?.amount_inc_tax
            mayIncome.text = topInvoicedData[position].May?.amount_inc_tax
            junIncome.text = topInvoicedData[position].June?.amount_inc_tax
            julyIncome.text = topInvoicedData[position].July?.amount_inc_tax
            augIncome.text = topInvoicedData[position].August?.amount_inc_tax
            sepIncome.text = topInvoicedData[position].September?.amount_inc_tax
            octIncome.text = topInvoicedData[position].October?.amount_inc_tax
            novIncome.text = topInvoicedData[position].November?.amount_inc_tax
            decIncome.text = topInvoicedData[position].December?.amount_inc_tax
            totalAmountExl.text = topInvoicedData[position].amount_exc_tax_total
            totalAmountIncl.text = topInvoicedData[position].amount_inc_tax_total
        }
    }

}