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
import com.minbio.erp.financial_management.accounting.fragments.ledger.models.LedgerGroupByAccountsEntry
import com.minbio.erp.utils.AppUtils

class LedgerGroupByItemAdapter(
    _ledgerEntries: List<LedgerGroupByAccountsEntry>?,
    _ledgerGroupByAccountingFragment: LedgerGroupByAccountingFragment,
    _ledgerItemPostion: Int
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var context: Context
    private var ledgerEntries = _ledgerEntries
    private var ledgerGroupByAccountingFragment = _ledgerGroupByAccountingFragment
    private var ledgerItemPostion = _ledgerItemPostion
    private lateinit var alertDialog: AlertDialog

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return Items(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_financial_accounting_ledger_groupby_item, parent, false)
        )
    }

    override fun getItemCount(): Int = ledgerEntries?.size!!

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is Items)
            holder.bind(position)
    }

    inner class Items(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var numTransaction: TextView = itemView.findViewById(R.id.numTransaction)
        private var date: TextView = itemView.findViewById(R.id.date)
        private var accountingDoc: TextView = itemView.findViewById(R.id.accountingDoc)
        private var label: TextView = itemView.findViewById(R.id.label)
        private var debit: TextView = itemView.findViewById(R.id.debit)
        private var credit: TextView = itemView.findViewById(R.id.credit)
        private var journal: TextView = itemView.findViewById(R.id.journal)
        private var edit: ImageView = itemView.findViewById(R.id.edit)
        private var delete: ImageView = itemView.findViewById(R.id.delete)

        fun bind(position: Int) {
            numTransaction.text = ledgerEntries!![position].transaction_id.toString()
            date.text = ledgerEntries!![position].date
            accountingDoc.text = ledgerEntries!![position].description
            label.text = ledgerEntries!![position].label
            debit.text = ledgerEntries!![position].debit.toString()
            credit.text = ledgerEntries!![position].credit.toString()
            journal.text = ledgerEntries!![position].journal

            edit.setOnClickListener {
                AppUtils.preventTwoClick(edit)
                ledgerGroupByAccountingFragment.editLedger(absoluteAdapterPosition,ledgerItemPostion)
            }

            delete.setOnClickListener {
                AppUtils.preventTwoClick(delete)
                alertDialog = AlertDialog.Builder(context)
                    .setMessage(context.resources.getString(R.string.deleteListItem))
                    .setPositiveButton(context.resources.getString(R.string.yes)) { _, _ ->
                        ledgerGroupByAccountingFragment.deleteLedgerLines(absoluteAdapterPosition, ledgerItemPostion)
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