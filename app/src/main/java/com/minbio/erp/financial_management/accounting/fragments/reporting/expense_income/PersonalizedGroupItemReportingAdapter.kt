package com.minbio.erp.financial_management.accounting.fragments.reporting.expense_income

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.minbio.erp.R
import com.minbio.erp.financial_management.accounting.fragments.reporting.expense_income.models.PersonalizedGroupReportAccounts

class PersonalizedGroupItemReportingAdapter(_accounts: List<PersonalizedGroupReportAccounts?>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var context: Context
    private var accounts = _accounts

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return Items(
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_fa_personalized_group_item_reporting,
                    parent,
                    false
                )
        )
    }

    override fun getItemCount(): Int = accounts.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is Items)
            holder.bind(position)
    }

    inner class Items(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var personalizedGroup: TextView = itemView.findViewById(R.id.personalizedGroup)
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


        @SuppressLint("SetTextI18n")
        fun bind(position: Int) {

            personalizedGroup.text = "${accounts[position]?.account_number} - ${accounts[position]?.label}"
            previousPeriod.text = accounts[position]?.previous_period_amount
            selectedPeriod.text = accounts[position]?.selected_period_amount
            Jan.text = accounts[position]?.report?.January
            Feb.text = accounts[position]?.report?.February
            Mar.text = accounts[position]?.report?.March
            Apr.text = accounts[position]?.report?.April
            May.text = accounts[position]?.report?.March
            Jun.text = accounts[position]?.report?.June
            Jul.text = accounts[position]?.report?.July
            Aug.text = accounts[position]?.report?.August
            Sep.text = accounts[position]?.report?.September
            Oct.text = accounts[position]?.report?.October
            Nov.text = accounts[position]?.report?.November
            Dec.text = accounts[position]?.report?.December
        }
    }

}