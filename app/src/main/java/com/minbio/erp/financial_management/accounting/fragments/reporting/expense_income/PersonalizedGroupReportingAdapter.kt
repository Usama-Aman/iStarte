package com.minbio.erp.financial_management.accounting.fragments.reporting.expense_income

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.minbio.erp.R
import com.minbio.erp.financial_management.accounting.fragments.reporting.expense_income.models.PersonalizedGroupReportingData

class PersonalizedGroupReportingAdapter(_personalizedGroupReportingData: MutableList<PersonalizedGroupReportingData>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var context: Context
    private var personalizedGroupReportingData = _personalizedGroupReportingData

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return Items(
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_financial_accounting_personalized_group_reporting,
                    parent,
                    false
                )
        )
    }

    override fun getItemCount(): Int = personalizedGroupReportingData.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is Items)
            holder.bind(position)
    }

    inner class Items(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var personalizedGroup: TextView = itemView.findViewById(R.id.personalizedGroup)
        private var balance: TextView = itemView.findViewById(R.id.balance)
        private var previousPeriod: TextView = itemView.findViewById(R.id.previousPeriod)
        private var selectedPeriod: TextView = itemView.findViewById(R.id.selectedPeriod)
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
        private var personalizedGroupRecyclerView: RecyclerView =
            itemView.findViewById(R.id.personalizedGroupRecyclerView)

        private lateinit var personalizedGroupItemReportingAdapter: PersonalizedGroupItemReportingAdapter

        fun bind(position: Int) {

            personalizedGroup.text = personalizedGroupReportingData[position].group
            balance.text = personalizedGroupReportingData[position].label
            previousPeriod.text = personalizedGroupReportingData[position].previous_period_amount
            selectedPeriod.text = personalizedGroupReportingData[position].selected_period_amount
            Jan.text = personalizedGroupReportingData[position].report?.January
            Feb.text = personalizedGroupReportingData[position].report?.February
            Mar.text = personalizedGroupReportingData[position].report?.March
            Apr.text = personalizedGroupReportingData[position].report?.April
            May.text = personalizedGroupReportingData[position].report?.March
            Jun.text = personalizedGroupReportingData[position].report?.June
            Jul.text = personalizedGroupReportingData[position].report?.July
            Aug.text = personalizedGroupReportingData[position].report?.August
            Sep.text = personalizedGroupReportingData[position].report?.September
            Oct.text = personalizedGroupReportingData[position].report?.October
            Nov.text = personalizedGroupReportingData[position].report?.November
            Dec.text = personalizedGroupReportingData[position].report?.December

            if (!personalizedGroupReportingData[position].accounts.isNullOrEmpty()) {
                personalizedGroupItemReportingAdapter =
                    PersonalizedGroupItemReportingAdapter(personalizedGroupReportingData[position].accounts)
                personalizedGroupRecyclerView.isNestedScrollingEnabled = false
                personalizedGroupRecyclerView.layoutManager = LinearLayoutManager(context)
                personalizedGroupRecyclerView.adapter = personalizedGroupItemReportingAdapter
            }

        }
    }

}