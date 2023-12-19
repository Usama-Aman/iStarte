package com.minbio.erp.financial_management.accounting.fragments.account_balance

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.minbio.erp.R

class AccountBalanceAdapter(_accountBalanceData: MutableList<AccountBalanceData?>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var context: Context
    private var accountBalanceData = _accountBalanceData

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return if (viewType == 0) Items(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_financial_accounting_account_balance, parent, false)
        )
        else
            Progress(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_pagination_progress, parent, false)
            )
    }

    override fun getItemCount(): Int = accountBalanceData.size

    override fun getItemViewType(position: Int): Int {
        return if (accountBalanceData[position] == null) 1 else 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is Items)
            holder.bind(position)
    }

    inner class Items(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var tvAccountLabel: TextView = itemView.findViewById(R.id.tvAccountLabel)
        private var accountingAccount: TextView = itemView.findViewById(R.id.accountingAccount)
        private var label: TextView = itemView.findViewById(R.id.label)
        private var openingBalance: TextView = itemView.findViewById(R.id.openingBalance)
        private var debit: TextView = itemView.findViewById(R.id.debit)
        private var credit: TextView = itemView.findViewById(R.id.credit)
        private var balance: TextView = itemView.findViewById(R.id.balance)
        private var subTotalLayout: LinearLayout = itemView.findViewById(R.id.subTotalLayout)
        private var tvSubTotalDebit: TextView = itemView.findViewById(R.id.tvSubTotalDebit)
        private var tvSubTotalCredit: TextView = itemView.findViewById(R.id.tvSubTotalCredit)
        private var tvSubTotalBalance: TextView = itemView.findViewById(R.id.tvSubTotalBalance)

        @SuppressLint("SetTextI18n")
        fun bind(position: Int) {
            tvAccountLabel.text =
                "${accountBalanceData[position]?.account_number} : ${accountBalanceData[position]?.chart_account_label}"
            accountingAccount.text = accountBalanceData[position]?.account_number
            label.text = accountBalanceData[position]?.chart_account_label
            openingBalance.text = accountBalanceData[position]?.opening_balance
            debit.text = accountBalanceData[position]?.debit
            credit.text = accountBalanceData[position]?.credit
            balance.text = accountBalanceData[position]?.balance
            tvSubTotalDebit.text = accountBalanceData[position]?.subtotal_debit
            tvSubTotalCredit.text = accountBalanceData[position]?.subtotal_credit
            tvSubTotalBalance.text = accountBalanceData[position]?.subtotal_balance
        }
    }

    inner class Progress(itemView: View) : RecyclerView.ViewHolder(itemView)


}