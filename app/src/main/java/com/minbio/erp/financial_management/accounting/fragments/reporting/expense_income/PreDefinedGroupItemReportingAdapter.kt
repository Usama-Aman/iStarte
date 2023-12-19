package com.minbio.erp.financial_management.accounting.fragments.reporting.expense_income

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.minbio.erp.R
import com.minbio.erp.financial_management.accounting.fragments.reporting.expense_income.models.PredefinedGroupsReporting

class PreDefinedGroupItemReportingAdapter(_accounts: List<PredefinedGroupsReporting>?) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var context: Context
    private var accounts = _accounts

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return Items(
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_fa_reporting_predefined_group_item,
                    parent,
                    false
                )
        )
    }

    override fun getItemCount(): Int = accounts?.size!!

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is Items)
            holder.bind(position)
    }

    inner class Items(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var personalizedGroup: TextView = itemView.findViewById(R.id.personalizedGroup)
        private var amount: TextView = itemView.findViewById(R.id.amount)

        @SuppressLint("SetTextI18n")
        fun bind(position: Int) {

            personalizedGroup.text =
                "${accounts!![position].account_number} - ${accounts!![position].label}"
            amount.text = accounts!![position].amount
        }
    }

}