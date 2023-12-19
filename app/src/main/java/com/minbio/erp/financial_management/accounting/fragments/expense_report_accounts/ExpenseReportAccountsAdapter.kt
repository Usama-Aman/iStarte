package com.minbio.erp.financial_management.accounting.fragments.expense_report_accounts

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.minbio.erp.R
import com.minbio.erp.financial_management.accounting.fragments.expense_report_accounts.models.ExpenseReportAccountsData

class ExpenseReportAccountsAdapter(
    _expenseReportAccountsFragment: ExpenseReportAccountsFragment,
    _expenseReportData: MutableList<ExpenseReportAccountsData?>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private lateinit var alertDialog: AlertDialog
    private lateinit var context: Context
    private var expenseReportAccountsFragment = _expenseReportAccountsFragment
    private var expenseReportData = _expenseReportData

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return if (viewType == 0) Items(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_financial_accounting_expense_report_account, parent, false)
        )
        else
            Progress(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_pagination_progress, parent, false)
            )
    }

    override fun getItemCount(): Int = expenseReportData.size

    override fun getItemViewType(position: Int): Int {
        return if (expenseReportData[position] == null) 1 else 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is Items)
            holder.bind(position)
    }

    inner class Items(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var code: TextView = itemView.findViewById(R.id.code)
        private var label: TextView = itemView.findViewById(R.id.label)
        private var accountingCode: TextView = itemView.findViewById(R.id.accountingCode)
        private val status: Switch = itemView.findViewById(R.id.status)
        private val edit: ImageView = itemView.findViewById(R.id.edit)
        private val delete: ImageView = itemView.findViewById(R.id.delete)

        fun bind(position: Int) {

            code.text = expenseReportData[position]?.code
            label.text = expenseReportData[position]?.label

            if (expenseReportData[position]?.chart_account_id!! > 0)
                accountingCode.text =
                    expenseReportData[position]?.chart_account_number + " - "+ expenseReportData[position]?.chart_account_label

            status.isChecked = expenseReportData[position]?.status == 1

            status.setOnClickListener {
                if (expenseReportData[position]?.status == 0)
                    expenseReportData[position]?.status = 1
                else
                    expenseReportData[position]?.status = 0

                expenseReportAccountsFragment.updateStatus(
                    expenseReportData[absoluteAdapterPosition]?.id!!,
                    absoluteAdapterPosition
                )
            }

            edit.setOnClickListener {
                val bundle = Bundle()
                bundle.putParcelable("data", expenseReportData[position])
                val fragment = AddExpenseReportAccountFragment()
                fragment.arguments = bundle
                expenseReportAccountsFragment.launchEditFragment(fragment)
            }

            delete.setOnClickListener {
                alertDialog = AlertDialog.Builder(context)
                    .setMessage(context.resources.getString(R.string.deleteListItem))
                    .setPositiveButton(context.resources.getString(R.string.yes)) { _, _ ->
                        expenseReportAccountsFragment.deleteExpenseReport(
                            expenseReportData[absoluteAdapterPosition]?.id!!,
                            absoluteAdapterPosition
                        )
                    }
                    .setNegativeButton(context.resources.getString(R.string.no)) { _, _ ->
                        alertDialog.dismiss()
                    }
                    .show()


            }

        }
    }

    inner class Progress(itemView: View) : RecyclerView.ViewHolder(itemView)


}