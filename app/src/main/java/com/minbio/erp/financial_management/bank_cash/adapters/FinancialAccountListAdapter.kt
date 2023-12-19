package com.minbio.erp.financial_management.bank_cash.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.minbio.erp.R
import com.minbio.erp.financial_management.bank_cash.fragments.financial.FinancialAccountListFragment
import com.minbio.erp.financial_management.bank_cash.fragments.financial.models.FinancialAccountListData

class FinancialAccountListAdapter(
    _financialAccountList: MutableList<FinancialAccountListData?>,
    _financialAccountListFragment: FinancialAccountListFragment
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private var financialAccountList = _financialAccountList
    private var financialAccountListFragment = _financialAccountListFragment
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return if (viewType == 0) Items(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_financial_accounts_list, parent, false)
        ) else
            Progress(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_pagination_progress, parent, false)
            )
    }

    override fun getItemCount(): Int = financialAccountList.size

    override fun getItemViewType(position: Int): Int {
        return if (financialAccountList[position] == null) 1 else 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is Items)
            holder.bind(position)
    }

    inner class Items(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val bankAccount: TextView = itemView.findViewById(R.id.bankAccount)
        private val Label: TextView = itemView.findViewById(R.id.Label)
        private val type: TextView = itemView.findViewById(R.id.type)
        private val number: TextView = itemView.findViewById(R.id.number)
        private val entries: TextView = itemView.findViewById(R.id.entries)
        private val status: TextView = itemView.findViewById(R.id.status)
        private val balance: TextView = itemView.findViewById(R.id.balance)
        private val edit: ImageView = itemView.findViewById(R.id.edit)


        fun bind(position: Int) {
            bankAccount.text = financialAccountList[position]?.bank_name
            Label.text = financialAccountList[position]?.label
            type.text = financialAccountList[position]?.account_type
            number.text = financialAccountList[position]?.account_no
            entries.text = financialAccountList[position]?.entries_to_reconcile
            status.text = financialAccountList[position]?.status
            balance.text = financialAccountList[position]?.initial_balance

            edit.setOnClickListener {
                financialAccountListFragment.editAccount(absoluteAdapterPosition)
            }
        }
    }

    inner class Progress(itemView: View) : RecyclerView.ViewHolder(itemView)


}