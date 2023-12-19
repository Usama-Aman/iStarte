package com.minbio.erp.financial_management.accounting.fragments.reporting.turn_over_collected

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.minbio.erp.R
import com.minbio.erp.financial_management.accounting.fragments.reporting.turn_over_collected.models.TOCollectedData
import com.minbio.erp.utils.Constants
import com.minbio.erp.utils.SharedPreference

class TurnOverCollectedAdapter(_toCollectedData: List<TOCollectedData>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var context: Context
    private var toCollectedData = _toCollectedData

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return Items(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_fa_turnover_collected, parent, false)
        )
    }

    override fun getItemCount(): Int = toCollectedData.size

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

        private var fatoctvAmountIncl: TextView = itemView.findViewById(R.id.fatoctvAmountIncl)

        fun bind(position: Int) {
            fatoctvAmountIncl.text = context!!.resources.getString(
                R.string.fatocLabelAmountIncl,
                SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
            )
            year.text = toCollectedData[position].year
            janExpense.text = toCollectedData[position].January
            febExpense.text = toCollectedData[position].February
            marExpense.text = toCollectedData[position].March
            aprExpense.text = toCollectedData[position].April
            mayExpense.text = toCollectedData[position].May
            junExpense.text = toCollectedData[position].June
            julyExpense.text = toCollectedData[position].July
            augExpense.text = toCollectedData[position].August
            sepExpense.text = toCollectedData[position].September
            octExpense.text = toCollectedData[position].October
            novExpense.text = toCollectedData[position].November
            decExpense.text = toCollectedData[position].December
            totalAmount.text = toCollectedData[position].amount_inc_tax_total
        }
    }

}