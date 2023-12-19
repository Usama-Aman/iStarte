package com.minbio.erp.financial_management.accounting.fragments.ledger

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.minbio.erp.R
import com.minbio.erp.financial_management.accounting.fragments.ledger.models.LedgerGroupByAccountsData

class LedgerGroupByAccountingAdapter(
    _ledgerData: MutableList<LedgerGroupByAccountsData?>,
    _ledgerGroupByAccountingFragment: LedgerGroupByAccountingFragment
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var context: Context
    private lateinit var ledgerGroupByItemAdapter: LedgerGroupByItemAdapter
    private var ledgerData = _ledgerData
    private var ledgerGroupByAccountingFragment = _ledgerGroupByAccountingFragment

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return if (viewType == 0) Items(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_financial_accounting_group_by_ledger, parent, false)
        )
        else
            Progress(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_pagination_progress, parent, false)
            )
    }

    override fun getItemCount(): Int = ledgerData.size

    override fun getItemViewType(position: Int): Int {
        return if (ledgerData[position] == null) 1 else 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is Items)
            holder.bind(position)
    }

    inner class Items(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var ledgerGroupByItemRecycler: RecyclerView =
            itemView.findViewById(R.id.ledgerGroupByItemRecycler)
        private var tvTotalAccountingAccount: TextView =
            itemView.findViewById(R.id.tvTotalAccountingAccount)
        private var tvDebitTotal: TextView = itemView.findViewById(R.id.tvDebitTotal)
        private var tvCreditTotal: TextView = itemView.findViewById(R.id.tvCreditTotal)
        private var tvDebitBalance: TextView = itemView.findViewById(R.id.tvDebitBalance)
        private var tvCreditBalance: TextView = itemView.findViewById(R.id.tvCreditBalance)
        private var tvAccountLabel: TextView = itemView.findViewById(R.id.tvAccountLabel)

        @SuppressLint("SetTextI18n")
        fun bind(position: Int) {

            if (!ledgerData[position]?.ledger_entries?.isNullOrEmpty()!!) {
                ledgerGroupByItemAdapter = LedgerGroupByItemAdapter(
                    ledgerData[position]?.ledger_entries,
                    ledgerGroupByAccountingFragment, absoluteAdapterPosition
                )
                ledgerGroupByItemRecycler.layoutManager = LinearLayoutManager(context)
                ledgerGroupByItemRecycler.hasFixedSize()
                ledgerGroupByItemRecycler.adapter = ledgerGroupByItemAdapter
            }

            tvTotalAccountingAccount.text = context.resources.getString(
                R.string.faledgerLabelTotalForAccountingAccount,
                ledgerData[position]?.account_number
            )
            tvDebitTotal.text = ledgerData[position]?.total_debit.toString()
            tvCreditTotal.text = ledgerData[position]?.total_credit.toString()
            tvAccountLabel.text =
                "${ledgerData[position]?.account_number} : ${ledgerData[position]?.chart_account_label}"
            tvDebitBalance.text = ledgerData[position]?.debit_balance.toString()
            tvCreditBalance.text = ledgerData[position]?.credit_balance.toString()

        }
    }

    inner class Progress(itemView: View) : RecyclerView.ViewHolder(itemView)


}