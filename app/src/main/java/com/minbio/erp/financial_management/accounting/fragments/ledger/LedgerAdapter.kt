package com.minbio.erp.financial_management.accounting.fragments.ledger

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.minbio.erp.R
import com.minbio.erp.financial_management.accounting.fragments.ledger.models.LedgerData
import com.minbio.erp.utils.AppUtils

class LedgerAdapter(
    _ledgerData: MutableList<LedgerData?>,
    _ledgerFragment: LedgerFragment
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var context: Context
    private lateinit var alertDialog: AlertDialog
    private var ledgerData = _ledgerData
    private var ledgerFragment = _ledgerFragment

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return if (viewType == 0) Items(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_financial_accounting_ledger, parent, false)
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

        private var numTransaction: TextView = itemView.findViewById(R.id.numTransaction)
        private var date: TextView = itemView.findViewById(R.id.date)
        private var accountingDoc: TextView = itemView.findViewById(R.id.accountingDoc)
        private var account: TextView = itemView.findViewById(R.id.account)
        private var subledgerAccount: TextView = itemView.findViewById(R.id.subledgerAccount)
        private var label: TextView = itemView.findViewById(R.id.label)
        private var debit: TextView = itemView.findViewById(R.id.debit)
        private var credit: TextView = itemView.findViewById(R.id.credit)
        private var journal: TextView = itemView.findViewById(R.id.journal)
        private var exportedDate: TextView = itemView.findViewById(R.id.exportedDate)
        private var edit: ImageView = itemView.findViewById(R.id.edit)
        private var delete: ImageView = itemView.findViewById(R.id.delete)

        fun bind(position: Int) {
            numTransaction.text = ledgerData[position]?.id.toString()
            date.text = ledgerData[position]?.date
            accountingDoc.text = ledgerData[position]?.description
            account.text = ledgerData[position]?.chart_account_number
            subledgerAccount.text = ledgerData[position]?.sub_ledger_account
            label.text = ledgerData[position]?.label
            debit.text = ledgerData[position]?.debit
            credit.text = ledgerData[position]?.credit
            journal.text = ledgerData[position]?.journal
            exportedDate.text = ledgerData[position]?.exported_date

            edit.setOnClickListener {
                AppUtils.preventTwoClick(edit)
                ledgerFragment.editLedger(absoluteAdapterPosition)
            }

            delete.setOnClickListener {
                AppUtils.preventTwoClick(delete)
                alertDialog = AlertDialog.Builder(context)
                    .setMessage(context.resources.getString(R.string.deleteListItem))
                    .setPositiveButton(context.resources.getString(R.string.yes)) { _, _ ->
                        ledgerFragment.deleteLedgerLines(absoluteAdapterPosition)
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