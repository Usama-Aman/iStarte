package com.minbio.erp.financial_management.accounting.fragments.expense_report_binding

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.minbio.erp.R
import com.minbio.erp.financial_management.accounting.fragments.expense_report_binding.models.ExpenseReportBoundData

class ExpenseBoundAdapter(
    _expenseReportBoundData: MutableList<ExpenseReportBoundData?>,
    _expenseBoundFragment: ExpenseBoundFragment
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var context: Context
    private var expenseReportBoundData = _expenseReportBoundData
    private var expenseBoundFragment = _expenseBoundFragment


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return if (viewType == 0) Items(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_financial_accounting_expense_bound, parent, false)
        )
        else
            Progress(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_pagination_progress, parent, false)
            )
    }

    override fun getItemCount(): Int = expenseReportBoundData.size

    override fun getItemViewType(position: Int): Int {
        return if (expenseReportBoundData[position] == null) 1 else 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is Items)
            holder.bind(position)
    }

    inner class Items(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var idLine: TextView = itemView.findViewById(R.id.idLine)
        private var expenseReport: TextView = itemView.findViewById(R.id.expenseReport)
        private var dateOfLine: TextView = itemView.findViewById(R.id.dateOfLine)
        private var typeOfFees: TextView = itemView.findViewById(R.id.typeOfFees)
        private var description: TextView = itemView.findViewById(R.id.description)
        private var amount: TextView = itemView.findViewById(R.id.amount)
        private var tax: TextView = itemView.findViewById(R.id.tax)
        private var account: TextView = itemView.findViewById(R.id.account)
        private var checkBox: CheckBox = itemView.findViewById(R.id.checkBox)

        fun bind(position: Int) {
            idLine.text = expenseReportBoundData[position]?.id.toString()
            expenseReport.text = expenseReportBoundData[position]?.expense_report_account_label
            dateOfLine.text = expenseReportBoundData[position]?.date
            typeOfFees.text = expenseReportBoundData[position]?.expense_report_account_label
            description.text = expenseReportBoundData[position]?.note
            amount.text = expenseReportBoundData[position]?.amount.toString()
            tax.text = expenseReportBoundData[position]?.vat_percentage
            account.text =
                expenseReportBoundData[position]?.chart_account_number + " - " + expenseReportBoundData[position]?.chart_account_label

            checkBox.isChecked = expenseReportBoundData[position]?.isChecked!!

            checkBox.setOnClickListener {
                expenseReportBoundData[position]?.isChecked =
                    !expenseReportBoundData[position]?.isChecked!!
            }

        }
    }

    inner class Progress(itemView: View) : RecyclerView.ViewHolder(itemView)


}