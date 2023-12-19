package com.minbio.erp.financial_management.accounting.fragments.reporting.expense_income

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.minbio.erp.R
import com.minbio.erp.financial_management.accounting.fragments.reporting.expense_income.models.PredefinedGroupsReportingData

class PreDefinedGroupReportingAdapter(
    _predefinedGroupsReportingData: MutableList<PredefinedGroupsReportingData>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var context: Context
    private var predefinedGroupsReportingData = _predefinedGroupsReportingData

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return Items(
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_financial_accounting_predefined_group_reporting,
                    parent,
                    false
                )
        )
    }

    override fun getItemCount(): Int = predefinedGroupsReportingData.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is Items)
            holder.bind(position)
    }

    inner class Items(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var groupLabel: TextView = itemView.findViewById(R.id.groupLabel)
        private var group: TextView = itemView.findViewById(R.id.group)
        private var groupTotal: TextView = itemView.findViewById(R.id.groupTotal)
        private var accountsRecyclerView: RecyclerView =
            itemView.findViewById(R.id.accountsRecyclerView)
        private lateinit var preDefinedGroupItemReportingAdapter: PreDefinedGroupItemReportingAdapter

        fun bind(position: Int) {

            groupLabel.text = predefinedGroupsReportingData[position].account_group
            group.text = predefinedGroupsReportingData[position].account_group
            groupTotal.text = predefinedGroupsReportingData[position].total

            if (!predefinedGroupsReportingData[position].accounts.isNullOrEmpty()) {
                preDefinedGroupItemReportingAdapter =
                    PreDefinedGroupItemReportingAdapter(predefinedGroupsReportingData[position].accounts)
                accountsRecyclerView.layoutManager = object : LinearLayoutManager(context) {
                    override fun canScrollVertically(): Boolean {
                        return false
                    }
                }
                accountsRecyclerView.adapter = preDefinedGroupItemReportingAdapter
            }

        }
    }

}