package com.minbio.erp.financial_management.accounting.fragments.registration_in_accounting

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.minbio.erp.R
import com.minbio.erp.financial_management.accounting.fragments.registration_in_accounting.models.JournalsData

class JournalsAdapter(
    _journalsData: MutableList<JournalsData?>,
    _isFinance: Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var context: Context
    private var isFinance = _isFinance
    private var journalsData = _journalsData

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return if (viewType == 0) Items(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_financial_accounting_registration_journals, parent, false)
        )
        else
            Progress(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_pagination_progress, parent, false)
            )
    }

    override fun getItemCount(): Int = journalsData.size

    override fun getItemViewType(position: Int): Int {
        return if (journalsData[position] == null) 1 else 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is Items)
            holder.bind(position)
    }

    inner class Items(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var date: TextView = itemView.findViewById(R.id.date)
        private var accountingDoc: TextView = itemView.findViewById(R.id.accountingDoc)
        private var accountingAccount: TextView = itemView.findViewById(R.id.accountingAccount)
        private var subledgerAccount: TextView = itemView.findViewById(R.id.subledgerAccount)
        private var labelOperation: TextView = itemView.findViewById(R.id.labelOperation)
        private var paymentType: TextView = itemView.findViewById(R.id.paymentType)
        private var debit: TextView = itemView.findViewById(R.id.debit)
        private var credit: TextView = itemView.findViewById(R.id.credit)

        fun bind(position: Int) {

            if (isFinance) {
                paymentType.visibility = View.VISIBLE
                paymentType.text = journalsData[position]?.payments_type
            } else
                paymentType.visibility = View.GONE

            date.text = journalsData[position]?.date
            accountingDoc.text = journalsData[position]?.id.toString()
            accountingAccount.text = journalsData[position]?.chart_account
            subledgerAccount.text = journalsData[position]?.subledger_chart_account
            labelOperation.text = journalsData[position]?.label_operation
            debit.text = journalsData[position]?.debit
            credit.text = journalsData[position]?.credit


        }
    }

    inner class Progress(itemView: View) : RecyclerView.ViewHolder(itemView)


}