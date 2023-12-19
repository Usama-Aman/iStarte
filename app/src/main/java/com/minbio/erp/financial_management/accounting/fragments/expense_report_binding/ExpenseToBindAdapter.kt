package com.minbio.erp.financial_management.accounting.fragments.expense_report_binding

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.minbio.erp.R
import com.minbio.erp.financial_management.accounting.fragments.expense_report_binding.models.ExpenseReportToBindData
import kotlinx.android.synthetic.main.item_sales_order.view.*

class ExpenseToBindAdapter(
    _expenseReportToBindData: MutableList<ExpenseReportToBindData?>,
    _expenseToBindFragment: ExpenseToBindFragment
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var context: Context
    private var expenseReportToBindData = _expenseReportToBindData
    private var expenseToBindFragment = _expenseToBindFragment


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return if (viewType == 0) Items(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_financial_accounting_expense_bind, parent, false)
        )
        else
            Progress(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_pagination_progress, parent, false)
            )
    }

    override fun getItemCount(): Int = expenseReportToBindData.size

    override fun getItemViewType(position: Int): Int {
        return if (expenseReportToBindData[position] == null) 1 else 0
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

        //        private var suggestedAccount: TextView = itemView.findViewById(R.id.suggestedAccount)
        private var bindLineAccount: TextView = itemView.findViewById(R.id.bindLineAccount)
        private var mainExpenseLayout: RelativeLayout =
            itemView.findViewById(R.id.mainExpenseLayout)

        fun bind(position: Int) {
            idLine.text = expenseReportToBindData[position]?.id.toString()
            expenseReport.text = expenseReportToBindData[position]?.expense_report_account_label
            dateOfLine.text = expenseReportToBindData[position]?.date
            typeOfFees.text = expenseReportToBindData[position]?.expense_report_account_label
            description.text = expenseReportToBindData[position]?.note
            amount.text = expenseReportToBindData[position]?.amount.toString()
            tax.text = expenseReportToBindData[position]?.vat_percentage
//            suggestedAccount.text= expenseReportToBindData[position]?.cha
            bindLineAccount.text = expenseReportToBindData[position]?.bind_account

            mainExpenseLayout.setOnClickListener {
                expenseToBindFragment.showAccountSpinner(position)
            }
        }
    }

    inner class Progress(itemView: View) : RecyclerView.ViewHolder(itemView)


}