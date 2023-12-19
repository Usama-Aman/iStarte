package com.minbio.erp.financial_management.bank_cash.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.minbio.erp.R
import com.minbio.erp.financial_management.bank_cash.fragments.financial.models.BankCashEntriesData

class FinancialEntriesAdapter(_bankCashEntriesData: MutableList<BankCashEntriesData?>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private var bankCashEntriesData = _bankCashEntriesData
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return if (viewType == 0) Items(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_financial_entries, parent, false)
        ) else
            Progress(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_pagination_progress, parent, false)
            )
    }

    override fun getItemCount(): Int = bankCashEntriesData.size

    override fun getItemViewType(position: Int): Int {
        return if (bankCashEntriesData[position] == null) 1 else 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is Items)
            holder.bind(position)
    }

    inner class Items(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val ref: TextView = itemView.findViewById(R.id.ref)
        private val description: TextView = itemView.findViewById(R.id.description)
        private val operDate: TextView = itemView.findViewById(R.id.operDate)
        private val valueDate: TextView = itemView.findViewById(R.id.valueDate)
        private val type: TextView = itemView.findViewById(R.id.type)
        private val number: TextView = itemView.findViewById(R.id.number)
        private val thirdParty: TextView = itemView.findViewById(R.id.thirdParty)
        private val bankAccount: TextView = itemView.findViewById(R.id.bankAccount)
        private val debit: TextView = itemView.findViewById(R.id.debit)
        private val credit: TextView = itemView.findViewById(R.id.credit)
        private val balance: TextView = itemView.findViewById(R.id.balance)
        private val accountStatement: TextView = itemView.findViewById(R.id.accountStatement)

        fun bind(position: Int) {
            ref.text = bankCashEntriesData[position]?.account?.reference
            description.text = bankCashEntriesData[position]?.description
            operDate.text = bankCashEntriesData[position]?.date
            valueDate.text = bankCashEntriesData[position]?.value_date
            type.text = bankCashEntriesData[position]?.payment_type
            number.text = bankCashEntriesData[position]?.number
//            thirdParty.text = bankCashEntriesData[position]?.
            bankAccount.text = bankCashEntriesData[position]?.account?.account_no
            debit.text = bankCashEntriesData[position]?.debit
            credit.text = bankCashEntriesData[position]?.credit
            balance.text = bankCashEntriesData[position]?.balance
//            accountStatement.text = bankCashEntriesData[position]?.
        }
    }

    inner class Progress(itemView: View) : RecyclerView.ViewHolder(itemView)


}