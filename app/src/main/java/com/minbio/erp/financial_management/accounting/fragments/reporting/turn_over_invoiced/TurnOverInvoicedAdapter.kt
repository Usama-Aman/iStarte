package com.minbio.erp.financial_management.accounting.fragments.reporting.turn_over_invoiced

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.minbio.erp.R
import com.minbio.erp.financial_management.accounting.fragments.reporting.turn_over_invoiced.models.TOInvoicedData
import com.minbio.erp.utils.Constants
import com.minbio.erp.utils.SharedPreference

class TurnOverInvoicedAdapter(_toInvoicedData: MutableList<TOInvoicedData>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var context: Context
    private var toInvoicedData = _toInvoicedData

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return Items(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_financial_accounting_turnover_invoiced, parent, false)
        )
    }

    override fun getItemCount(): Int = toInvoicedData.size

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
        private var fatoitvAmountExcl: TextView = itemView.findViewById(R.id.fatoitvAmountExcl)
        private var fatoitvAmountIncl: TextView = itemView.findViewById(R.id.fatoitvAmountIncl)

        fun bind(position: Int) {
            fatoitvAmountExcl.text = context!!.resources.getString(
                R.string.fatoiLabelAmountExcl,
                SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
            )
            fatoitvAmountIncl.text = context!!.resources.getString(
                R.string.fatoiLabelAmountIncl,
                SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
            )
            year.text = toInvoicedData[position].year
            janExpense.text = toInvoicedData[position].January?.amount_exc_tax
            febExpense.text = toInvoicedData[position].February?.amount_exc_tax
            marExpense.text = toInvoicedData[position].March?.amount_exc_tax
            aprExpense.text = toInvoicedData[position].April?.amount_exc_tax
            mayExpense.text = toInvoicedData[position].May?.amount_exc_tax
            junExpense.text = toInvoicedData[position].June?.amount_exc_tax
            julyExpense.text = toInvoicedData[position].July?.amount_exc_tax
            augExpense.text = toInvoicedData[position].August?.amount_exc_tax
            sepExpense.text = toInvoicedData[position].September?.amount_exc_tax
            octExpense.text = toInvoicedData[position].October?.amount_exc_tax
            novExpense.text = toInvoicedData[position].November?.amount_exc_tax
            decExpense.text = toInvoicedData[position].December?.amount_exc_tax
            janIncome.text = toInvoicedData[position].January?.amount_inc_tax
            febIncome.text = toInvoicedData[position].February?.amount_inc_tax
            marIncome.text = toInvoicedData[position].March?.amount_inc_tax
            aprIncome.text = toInvoicedData[position].April?.amount_inc_tax
            mayIncome.text = toInvoicedData[position].May?.amount_inc_tax
            junIncome.text = toInvoicedData[position].June?.amount_inc_tax
            julyIncome.text = toInvoicedData[position].July?.amount_inc_tax
            augIncome.text = toInvoicedData[position].August?.amount_inc_tax
            sepIncome.text = toInvoicedData[position].September?.amount_inc_tax
            octIncome.text = toInvoicedData[position].October?.amount_inc_tax
            novIncome.text = toInvoicedData[position].November?.amount_inc_tax
            decIncome.text = toInvoicedData[position].December?.amount_inc_tax
            totalAmountExl.text = toInvoicedData[position].amount_exc_tax_total
            totalAmountIncl.text = toInvoicedData[position].amount_inc_tax_total

        }
    }

}