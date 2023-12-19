package com.minbio.erp.financial_management.accounting.fragments.reporting.purchase_turn_over_collected

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.minbio.erp.R
import com.minbio.erp.financial_management.accounting.fragments.reporting.purchase_turn_over_collected.models.TOPCollectedThirdPartyData

class PurchaseTurnOverCollectedThirdPartyAdapter(_topCollectedThirdPartyData: MutableList<TOPCollectedThirdPartyData>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var context: Context
    private var topCollectedThirdPartyData = _topCollectedThirdPartyData

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return Items(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_fa_turnover_collected_third_parties, parent, false)
        )
    }

    override fun getItemCount(): Int = topCollectedThirdPartyData.size

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
            company.text = topCollectedThirdPartyData!![position].supplier_company_name
            country.text = topCollectedThirdPartyData!![position].country_name
            amountIncl.text = topCollectedThirdPartyData!![position].amount_inc_tax
            percentage.text = "${topCollectedThirdPartyData!![position].percentage}%"
        }
    }

}