package com.minbio.erp.financial_management.accounting.fragments.reporting.turn_over_collected

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.minbio.erp.R
import com.minbio.erp.financial_management.accounting.fragments.reporting.turn_over_collected.models.TOCollectedThirdPartyData

class TurnOverCollectedThirdPartyAdapter(_toCollectedThirdPartyData: MutableList<TOCollectedThirdPartyData>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var context: Context
    private var toCollectedThirdPartyData = _toCollectedThirdPartyData

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return Items(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_fa_turnover_collected_third_parties, parent, false)
        )
    }

    override fun getItemCount(): Int = toCollectedThirdPartyData.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is Items)
            holder.bind(position)
    }

    inner class Items(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var company: TextView = itemView.findViewById(R.id.company)
        private var country: TextView = itemView.findViewById(R.id.country)
        private var amountIncl: TextView = itemView.findViewById(R.id.amountIncl)
        private var percentage: TextView = itemView.findViewById(R.id.percentage)

        @SuppressLint("SetTextI18n")
        fun bind(position: Int) {
            company.text = toCollectedThirdPartyData[position].customer_name
            country.text = toCollectedThirdPartyData[position].country_name
            amountIncl.text = toCollectedThirdPartyData[position].amount_inc_tax
            percentage.text = "${toCollectedThirdPartyData[position].percentage}%"
        }
    }

}