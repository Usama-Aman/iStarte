package com.minbio.erp.financial_management.accounting.fragments.reporting.turn_over_collected

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.minbio.erp.R
import com.minbio.erp.financial_management.accounting.fragments.reporting.turn_over_collected.models.TOCollectedUserData

class TurnOverCollectedUserAdapter(_toCollectedUserData: MutableList<TOCollectedUserData>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var context: Context
    private var toCollectedUserData = _toCollectedUserData

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return Items(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_fa_turnover_collected_user, parent, false)
        )
    }

    override fun getItemCount(): Int = toCollectedUserData.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is Items)
            holder.bind(position)
    }

    inner class Items(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var user: TextView = itemView.findViewById(R.id.user)
        private var amountIncl: TextView = itemView.findViewById(R.id.amountIncl)
        private var percentage: TextView = itemView.findViewById(R.id.percentage)

        @SuppressLint("SetTextI18n")
        fun bind(position: Int) {
            user.text = toCollectedUserData[position].user_name
            amountIncl.text = toCollectedUserData[position].amount_inc_tax
            percentage.text = "${toCollectedUserData!![position].percentage} + %"
        }
    }

}