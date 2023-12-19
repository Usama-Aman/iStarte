package com.minbio.erp.financial_management.accounting.fragments.reporting.expense_income

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.minbio.erp.R
import com.minbio.erp.financial_management.accounting.fragments.reporting.expense_income.models.ExpenseIncomeReportingData
import com.minbio.erp.utils.Constants
import com.minbio.erp.utils.SharedPreference

class ExpenseIncomeReportingAdapter(_expenseIncomeReportingData: MutableList<ExpenseIncomeReportingData>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private lateinit var context: Context
        private var expenseIncomeReportingData = _expenseIncomeReportingData

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            context = parent.context
            return Items(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_financial_accounting_income_expense_reporting, parent, false)
            )
        }

        override fun getItemCount(): Int = expenseIncomeReportingData.size

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
            private var totalExpense: TextView = itemView.findViewById(R.id.totalExpense)
            private var totalIncome: TextView = itemView.findViewById(R.id.totalIncome)
            private var totalAccountingResult: TextView =
                itemView.findViewById(R.id.totalAccountingResult)
            private var farietvExpense: TextView = itemView.findViewById(R.id.farietvExpense)
            private var farietvIncome: TextView = itemView.findViewById(R.id.farietvIncome)

            fun bind(position: Int) {
                farietvExpense.text = context!!.resources.getString(
                    R.string.farieLabelExpense,
                    SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
                )
                farietvIncome.text = context!!.resources.getString(
                    R.string.farieLabelIncome,
                    SharedPreference.getSimpleString(context!!, Constants.defaultCurrency)
                )


                year.text = expenseIncomeReportingData[position].year
                janExpense.text = expenseIncomeReportingData[position].January?.expense
                febExpense.text = expenseIncomeReportingData[position].February?.expense
                marExpense.text = expenseIncomeReportingData[position].March?.expense
                aprExpense.text = expenseIncomeReportingData[position].April?.expense
                mayExpense.text = expenseIncomeReportingData[position].May?.expense
                junExpense.text = expenseIncomeReportingData[position].June?.expense
                julyExpense.text = expenseIncomeReportingData[position].July?.expense
                augExpense.text = expenseIncomeReportingData[position].August?.expense
                sepExpense.text = expenseIncomeReportingData[position].September?.expense
                octExpense.text = expenseIncomeReportingData[position].October?.expense
                novExpense.text = expenseIncomeReportingData[position].November?.expense
                decExpense.text = expenseIncomeReportingData[position].December?.expense
                janIncome.text = expenseIncomeReportingData[position].January?.income
                febIncome.text = expenseIncomeReportingData[position].February?.income
                marIncome.text = expenseIncomeReportingData[position].March?.income
                aprIncome.text = expenseIncomeReportingData[position].April?.income
                mayIncome.text = expenseIncomeReportingData[position].May?.income
                junIncome.text = expenseIncomeReportingData[position].June?.income
                julyIncome.text = expenseIncomeReportingData[position].July?.income
                augIncome.text = expenseIncomeReportingData[position].August?.income
                sepIncome.text = expenseIncomeReportingData[position].September?.income
                octIncome.text = expenseIncomeReportingData[position].October?.income
                novIncome.text = expenseIncomeReportingData[position].November?.income
                decIncome.text = expenseIncomeReportingData[position].December?.income
                totalExpense.text = expenseIncomeReportingData[position].expense_total
                totalIncome.text = expenseIncomeReportingData[position].income_total
                totalAccountingResult.text = expenseIncomeReportingData[position].accounting_result

            }
        }

    }