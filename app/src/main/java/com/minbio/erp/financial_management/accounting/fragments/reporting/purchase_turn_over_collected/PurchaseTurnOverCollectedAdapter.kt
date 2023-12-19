package com.minbio.erp.financial_management.accounting.fragments.reporting.purchase_turn_over_collected

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.minbio.erp.R
import com.minbio.erp.financial_management.accounting.fragments.reporting.purchase_turn_over_collected.models.TOPCollectedData
import com.minbio.erp.utils.Constants
import com.minbio.erp.utils.SharedPreference

class PurchaseTurnOverCollectedAdapter(_topCollectedData: MutableList<TOPCollectedData>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var context: Context
    private var topCollectedData = _topCollectedData

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return Items(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_fa_purchase_turnover_collected, parent, false)
        )
    }

    override fun getItemCount(): Int = topCollectedData.size

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
        private var totalAmount: TextView = itemView.findViewById(R.id.totalAmount)
        private var faptoctvAmountIncl: TextView = itemView.findViewById(R.id.faptoctvAmountIncl)

        fun bind(position: Int) {
            faptoctvAmountIncl.text = context!!.resources.getString(
                R.string.faptocLabelAmountIncl,
                SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
            )

            year.text = topCollectedData[position].year
            janExpense.text = topCollectedData[position].January
            febExpense.text = topCollectedData[position].February
            marExpense.text = topCollectedData[position].March
            aprExpense.text = topCollectedData[position].April
            mayExpense.text = topCollectedData[position].May
            junExpense.text = topCollectedData[position].June
            julyExpense.text = topCollectedData[position].July
            augExpense.text = topCollectedData[position].August
            sepExpense.text = topCollectedData[position].September
            octExpense.text = topCollectedData[position].October
            novExpense.text = topCollectedData[position].November
            decExpense.text = topCollectedData[position].December
            totalAmount.text = topCollectedData[position].amount_inc_tax_total

        }
    }

}